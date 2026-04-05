package com.campus.modules.order.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.order.dto.CartOrderCreateRequest;
import com.campus.modules.order.dto.OrderDetailDTO;
import com.campus.modules.order.dto.OrderItemDTO;
import com.campus.modules.order.dto.OrderListItemDTO;
import com.campus.modules.order.dto.OrderLogDTO;
import com.campus.modules.order.entity.OrderItem;
import com.campus.modules.order.entity.OrderLog;
import com.campus.modules.order.entity.OrderMain;
import com.campus.modules.order.mapper.OrderLogRepository;
import com.campus.modules.order.mapper.OrderItemRepository;
import com.campus.modules.order.mapper.OrderRepository;
import com.campus.modules.order.service.OrderService;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderLogRepository orderLogRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderLogRepository orderLogRepository,
                            OrderItemRepository orderItemRepository,
                            UserAccountRepository userAccountRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderLogRepository = orderLogRepository;
        this.orderItemRepository = orderItemRepository;
        this.userAccountRepository = userAccountRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Long createOrder(Long productId, Integer quantity) {
        Long buyerId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (buyerId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        if (productId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在");
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在");
        int qty = quantity == null || quantity < 1 ? 1 : quantity;
        if (product.getSellerId() != null && product.getSellerId().equals(buyerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能购买自己发布的商品");
        }
        int total = (product.getPrice() == null ? 0 : product.getPrice()) * qty;
        OrderMain order = new OrderMain();
        order.setOrderNo("O" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setProductId(product.getId());
        order.setProductTitle(product.getTitle() != null ? product.getTitle() : "");
        order.setProductCoverUrl(product.getCoverUrl());
        order.setPrice(product.getPrice());
        order.setQuantity(qty);
        order.setTotalAmount(total);
        order.setDeliveryTypeText(product.getDeliveryTypeText());
        order.setStatus("WAIT_PAY");
        order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        // 单商品订单也写入 1 条明细，便于前端统一展示
        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(product.getId());
        item.setProductTitle(order.getProductTitle());
        item.setProductCoverUrl(order.getProductCoverUrl());
        item.setPrice(order.getPrice() == null ? 0 : order.getPrice());
        item.setQuantity(order.getQuantity() == null ? 1 : order.getQuantity());
        item.setTotalAmount(order.getTotalAmount() == null ? 0 : order.getTotalAmount());
        orderItemRepository.save(item);
        addLog(order.getId(), "WAIT_PAY", "订单已创建，待支付");
        return order.getId();
    }

    @Override
    public Long createOrderFromCart(CartOrderCreateRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请先选择要结算的商品");
        }
        Long buyerId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (buyerId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");

        // 加载商品信息并校验
        List<CartOrderCreateRequest.Item> items = request.getItems().stream()
                .filter(i -> i.getProductId() != null && i.getQuantity() != null && i.getQuantity() > 0)
                .toList();
        if (items.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请先选择要结算的商品");

        List<Long> productIds = items.stream().map(CartOrderCreateRequest.Item::getProductId).toList();
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<Product> products = items.stream()
                .map(i -> productMap.get(i.getProductId()))
                .filter(Objects::nonNull)
                .toList();
        if (products.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在");

        // 校验不能购买自己发布的商品
        for (Product p : products) {
            if (p.getSellerId() != null && Objects.equals(p.getSellerId(), buyerId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能购买自己发布的商品");
            }
        }

        // 为本次结算生成一个 groupId，用于买家端聚合展示
        String groupId = "G" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);

        // 按卖家拆分，分别生成订单，每个订单挂在同一个 groupId 下
        Map<Long, List<CartOrderCreateRequest.Item>> itemsBySeller = items.stream()
                .map(i -> {
                    Product p = productMap.get(i.getProductId());
                    if (p == null || p.getSellerId() == null) return null;
                    return Map.entry(p.getSellerId(), i);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        if (itemsBySeller.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "结算商品异常");
        }

        Long firstOrderId = null;

        for (Map.Entry<Long, List<CartOrderCreateRequest.Item>> entry : itemsBySeller.entrySet()) {
            Long sellerId = entry.getKey();
            List<CartOrderCreateRequest.Item> sellerItems = entry.getValue();

            int totalQuantity = 0;
            int totalAmount = 0;
            List<Product> sellerProducts = sellerItems.stream()
                    .map(i -> productMap.get(i.getProductId()))
                    .filter(Objects::nonNull)
                    .toList();

            for (CartOrderCreateRequest.Item item : sellerItems) {
                Product p = productMap.get(item.getProductId());
                if (p == null) continue;
                int qty = item.getQuantity() == null || item.getQuantity() < 1 ? 1 : item.getQuantity();
                int price = p.getPrice() == null ? 0 : p.getPrice();
                totalQuantity += qty;
                totalAmount += price * qty;
            }
            if (totalQuantity <= 0 || totalAmount <= 0) {
                continue;
            }

            Product first = sellerProducts.get(0);
            String title = first.getTitle() != null ? first.getTitle() : "";
            if (sellerProducts.size() > 1) {
                title = title + " 等" + sellerProducts.size() + "件商品";
            }

            OrderMain order = new OrderMain();
            order.setOrderNo("O" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));
            order.setBuyerId(buyerId);
            order.setSellerId(sellerId);
            order.setGroupId(groupId);
            order.setProductId(first.getId());
            order.setProductTitle(title);
            order.setProductCoverUrl(first.getCoverUrl());
            order.setPrice(first.getPrice());
            order.setQuantity(totalQuantity);
            order.setTotalAmount(totalAmount);
            order.setDeliveryTypeText(first.getDeliveryTypeText());
            order.setStatus("WAIT_PAY");
            order.setCreatedAt(LocalDateTime.now());
            order = orderRepository.save(order);

            for (CartOrderCreateRequest.Item i : sellerItems) {
                Product p = productMap.get(i.getProductId());
                if (p == null) continue;
                int qty = i.getQuantity() == null || i.getQuantity() < 1 ? 1 : i.getQuantity();
                int price = p.getPrice() == null ? 0 : p.getPrice();
                OrderItem oi = new OrderItem();
                oi.setOrderId(order.getId());
                oi.setProductId(p.getId());
                oi.setProductTitle(p.getTitle() != null ? p.getTitle() : "");
                oi.setProductCoverUrl(p.getCoverUrl());
                oi.setPrice(price);
                oi.setQuantity(qty);
                oi.setTotalAmount(price * qty);
                orderItemRepository.save(oi);
            }
            addLog(order.getId(), "WAIT_PAY", "购物车结算订单已创建，待支付");

            if (firstOrderId == null) {
                firstOrderId = order.getId();
            }
        }

        if (firstOrderId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "结算失败，请稍后重试");
        }
        return firstOrderId;
    }

    @Override
    public PageResult<OrderListItemDTO> pageUserOrders(String status, String keyword, String startTime, String endTime, long pageNum, long pageSize) {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            return PageResults.empty(pageNum, pageSize);
        }
        return pageOrders("USER", currentUserId, null, status, keyword, startTime, endTime, pageNum, pageSize);
    }

    @Override
    public PageResult<OrderListItemDTO> pageSellerOrders(String status, String keyword, String startTime, String endTime, long pageNum, long pageSize) {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            return PageResults.empty(pageNum, pageSize);
        }
        return pageOrders("SELLER", null, currentUserId, status, keyword, startTime, endTime, pageNum, pageSize);
    }

    private PageResult<OrderListItemDTO> pageOrders(String role,
                                                    Long buyerId,
                                                    Long sellerId,
                                                    String status,
                                                    String keyword,
                                                    String startTime,
                                                    String endTime,
                                                    long pageNum,
                                                    long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Specification<OrderMain> spec = (root, q, cb) -> cb.conjunction();
        if (buyerId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("buyerId"), buyerId));
        }
        if (sellerId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("sellerId"), sellerId));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(root.get("orderNo"), like),
                    cb.like(root.get("productTitle"), like)
            ));
        }

        LocalDateTime start = parseStart(startTime);
        if (start != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), start));
        }
        LocalDateTime end = parseEnd(endTime);
        if (end != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), end));
        }

        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps, Sort.by("createdAt").descending());
        Page<OrderMain> page = orderRepository.findAll(spec, pageable);

        Map<Long, String> nameMap = loadUserNameMap(page.getContent());

        return PageResults.from(page, pn, ps, o -> toListItem(o, role, nameMap));
    }

    @Override
    public OrderDetailDTO getDetail(Long id, String role) {
        OrderMain base = orderRepository.findById(id).orElse(null);
        if (base == null) return null;

        // 买家视角：如果是购物车结算产生的 groupId，则聚合同一 group 下的所有卖家订单
        if ("USER".equalsIgnoreCase(role) && base.getGroupId() != null && !base.getGroupId().isBlank()) {
            List<OrderMain> groupOrders = orderRepository.findByGroupId(base.getGroupId());
            if (groupOrders == null || groupOrders.isEmpty()) {
                groupOrders = List.of(base);
            }

            Map<Long, String> nameMap = loadUserNameMap(groupOrders);

            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setId(base.getId());
            dto.setOrderNo(base.getOrderNo());
            dto.setRole(role);
            dto.setStatus(base.getStatus());
            dto.setCreatedAt(DateTimes.format(base.getCreatedAt()));

            // 汇总总金额与商品标题（简单取第一条标题）
            dto.setProductTitle(base.getProductTitle());
            dto.setProductCoverUrl(base.getProductCoverUrl());
            dto.setPrice(base.getPrice());
            dto.setQuantity(base.getQuantity());
            int sumAmount = groupOrders.stream()
                    .map(OrderMain::getTotalAmount)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();
            dto.setTotalAmount(sumAmount);

            dto.setBuyerName(nameMap.getOrDefault(base.getBuyerId(), ""));
            dto.setSellerName(null); // 多卖家场景下不再用单一卖家名称
            dto.setDeliveryTypeText(null);
            dto.setAddress(base.getAddress());
            dto.setLogisticsNo(base.getLogisticsNo());

            // 组装所有子订单的明细，每一条带上对应卖家名称与交易方式
            List<OrderItemDTO> allItems = new java.util.ArrayList<>();
            for (OrderMain order : groupOrders) {
                String buyerName = nameMap.getOrDefault(order.getBuyerId(), "");
                String sellerName = nameMap.getOrDefault(order.getSellerId(), "");
                String deliveryTypeText = order.getDeliveryTypeText();

                List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
                if (orderItems != null && !orderItems.isEmpty()) {
                    for (OrderItem oi : orderItems) {
                        OrderItemDTO it = new OrderItemDTO();
                        it.setProductId(oi.getProductId());
                        it.setProductTitle(oi.getProductTitle());
                        it.setProductCoverUrl(oi.getProductCoverUrl());
                        it.setPrice(oi.getPrice());
                        it.setQuantity(oi.getQuantity());
                        it.setTotalAmount(oi.getTotalAmount());
                        it.setBuyerName(buyerName);
                        it.setSellerName(sellerName);
                        it.setDeliveryTypeText(deliveryTypeText);
                        allItems.add(it);
                    }
                } else {
                    // 兼容历史数据：没有明细时用 order_main 兜底生成 1 条
                    OrderItemDTO it = new OrderItemDTO();
                    it.setProductId(order.getProductId());
                    it.setProductTitle(order.getProductTitle());
                    it.setProductCoverUrl(order.getProductCoverUrl());
                    it.setPrice(order.getPrice());
                    it.setQuantity(order.getQuantity());
                    it.setTotalAmount(order.getTotalAmount());
                    it.setBuyerName(buyerName);
                    it.setSellerName(sellerName);
                    it.setDeliveryTypeText(deliveryTypeText);
                    allItems.add(it);
                }
            }
            dto.setItems(allItems);

            dto.setLogs(listLogs(id));
            return dto;
        }

        // 卖家视角或普通订单：保持原有单订单逻辑
        OrderMain order = base;
        Map<Long, String> nameMap = loadUserNameMap(List.of(order));

        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setRole(role);
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(DateTimes.format(order.getCreatedAt()));

        dto.setProductTitle(order.getProductTitle());
        dto.setProductCoverUrl(order.getProductCoverUrl());
        dto.setPrice(order.getPrice());
        dto.setQuantity(order.getQuantity());
        dto.setTotalAmount(order.getTotalAmount());

        dto.setBuyerName(nameMap.getOrDefault(order.getBuyerId(), ""));
        dto.setSellerName(nameMap.getOrDefault(order.getSellerId(), ""));
        dto.setDeliveryTypeText(order.getDeliveryTypeText());
        dto.setAddress(order.getAddress());
        dto.setLogisticsNo(order.getLogisticsNo());

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDTO> itemDTOs;
        if (orderItems != null && !orderItems.isEmpty()) {
            final String buyerName = dto.getBuyerName();
            final String sellerName = dto.getSellerName();
            final String deliveryTypeText = dto.getDeliveryTypeText();
            itemDTOs = orderItems.stream().map(oi -> {
                OrderItemDTO it = new OrderItemDTO();
                it.setProductId(oi.getProductId());
                it.setProductTitle(oi.getProductTitle());
                it.setProductCoverUrl(oi.getProductCoverUrl());
                it.setPrice(oi.getPrice());
                it.setQuantity(oi.getQuantity());
                it.setTotalAmount(oi.getTotalAmount());
                it.setBuyerName(buyerName);
                it.setSellerName(sellerName);
                it.setDeliveryTypeText(deliveryTypeText);
                return it;
            }).toList();
        } else {
            // 兼容历史数据：没有明细时用 order_main 兜底生成 1 条
            OrderItemDTO it = new OrderItemDTO();
            it.setProductId(order.getProductId());
            it.setProductTitle(order.getProductTitle());
            it.setProductCoverUrl(order.getProductCoverUrl());
            it.setPrice(order.getPrice());
            it.setQuantity(order.getQuantity());
            it.setTotalAmount(order.getTotalAmount());
            it.setBuyerName(dto.getBuyerName());
            it.setSellerName(dto.getSellerName());
            it.setDeliveryTypeText(dto.getDeliveryTypeText());
            itemDTOs = List.of(it);
        }
        dto.setItems(itemDTOs);

        dto.setLogs(listLogs(id));
        return dto;
    }

    @Override
    public List<OrderLogDTO> listLogs(Long id) {
        return orderLogRepository.findByOrderIdOrderByCreatedAtAsc(id).stream().map(l -> {
            OrderLogDTO dto = new OrderLogDTO();
            dto.setStatus(l.getStatus());
            dto.setMessage(l.getMessage());
            dto.setCreatedAt(DateTimes.format(l.getCreatedAt()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void cancelOrder(Long id, String role) {
        OrderMain order = orderRepository.findById(id).orElse(null);
        if (order == null) return;

        // 买家端看到的是聚合订单（groupId 下的第一个子订单），取消时需要把同一 groupId 的子订单一起取消
        if ("USER".equalsIgnoreCase(role)
                && order.getGroupId() != null
                && !order.getGroupId().isBlank()) {
            List<OrderMain> groupOrders = orderRepository.findByGroupId(order.getGroupId());
            for (OrderMain o : groupOrders) {
                if (o == null) continue;
                if (!"WAIT_PAY".equals(o.getStatus())) continue;
                o.setStatus("CANCELLED");
                orderRepository.save(o);
                addLog(o.getId(), "CANCELLED", "订单已取消");
            }
            return;
        }

        updateStatus(id, "CANCELLED", "订单已取消");
    }

    @Override
    public void confirmReceive(Long id) {
        updateStatus(id, "FINISHED", "已确认收货");
    }

    @Override
    public void applyAfterSale(Long id) {
        updateStatus(id, "AFTER_SALE", "已申请售后");
    }

    @Override
    public void shipOrder(Long id, String logisticsNo) {
        OrderMain order = orderRepository.findById(id).orElse(null);
        if (order == null) return;
        order.setLogisticsNo(logisticsNo);
        order.setStatus("SHIPPED");
        orderRepository.save(order);
        addLog(id, "SHIPPED", "卖家已发货");
    }

    @Override
    public void payOrder(Long id, String method) {
        OrderMain order = orderRepository.findById(id).orElse(null);
        if (order == null) return;
        if (!"WAIT_PAY".equals(order.getStatus())) return;
        order.setPaymentMethod(method);
        order.setPaidAt(LocalDateTime.now());
        order.setStatus("WAIT_SHIP");
        orderRepository.save(order);
        String text = (method == null || method.isBlank()) ? "在线支付" : method;
        addLog(id, "WAIT_SHIP", "已支付（支付方式：" + text + "），待卖家发货");
    }

    private OrderListItemDTO toListItem(OrderMain o, String role, Map<Long, String> nameMap) {
        OrderListItemDTO dto = new OrderListItemDTO();
        dto.setId(o.getId());
        dto.setOrderNo(o.getOrderNo());
        dto.setRole(role);
        dto.setProductTitle(o.getProductTitle());
        dto.setProductCoverUrl(o.getProductCoverUrl());
        dto.setTotalAmount(o.getTotalAmount());
        dto.setDeliveryTypeText(o.getDeliveryTypeText());
        dto.setStatus(o.getStatus());
        dto.setCreatedAt(DateTimes.format(o.getCreatedAt()));

        if ("USER".equalsIgnoreCase(role)) {
            dto.setOtherPartyName(nameMap.getOrDefault(o.getSellerId(), ""));
        } else {
            dto.setOtherPartyName(nameMap.getOrDefault(o.getBuyerId(), ""));
        }
        return dto;
    }

    private Map<Long, String> loadUserNameMap(List<OrderMain> orders) {
        Set<Long> ids = orders.stream()
                .flatMap(o -> java.util.stream.Stream.of(o.getBuyerId(), o.getSellerId()))
                .collect(Collectors.toSet());
        return userAccountRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(UserAccount::getId,
                        u -> u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername(),
                        (a, b) -> a));
    }

    private void updateStatus(Long id, String status, String message) {
        OrderMain order = orderRepository.findById(id).orElse(null);
        if (order == null) return;
        order.setStatus(status);
        orderRepository.save(order);
        addLog(id, status, message);
    }

    private void addLog(Long orderId, String status, String message) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setStatus(status);
        log.setMessage(message);
        log.setCreatedAt(LocalDateTime.now());
        orderLogRepository.save(log);
    }

    private LocalDateTime parseStart(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date.trim()).atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseEnd(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date.trim()).atTime(23, 59, 59);
        } catch (Exception e) {
            return null;
        }
    }
}

