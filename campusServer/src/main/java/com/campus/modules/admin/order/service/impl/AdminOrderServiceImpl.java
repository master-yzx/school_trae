package com.campus.modules.admin.order.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.common.utils.UploadPaths;
import com.campus.modules.admin.order.dto.AdminOrderDTO;
import com.campus.modules.admin.order.service.AdminOrderService;
import com.campus.modules.order.entity.OrderMain;
import com.campus.modules.order.mapper.OrderRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final UserAccountRepository userAccountRepository;

    public AdminOrderServiceImpl(OrderRepository orderRepository, UserAccountRepository userAccountRepository) {
        this.orderRepository = orderRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public PageResult<AdminOrderDTO> pageList(String status, String buyerKeyword, String sellerKeyword,
                                              String orderNo, String startTime, String endTime,
                                              long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Specification<OrderMain> spec = (root, q, cb) -> cb.conjunction();

        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (orderNo != null && !orderNo.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(root.get("orderNo"), "%" + orderNo.trim() + "%"));
        }
        if (buyerKeyword != null && !buyerKeyword.isBlank()) {
            Set<Long> buyerIds = userAccountRepository
                    .findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(buyerKeyword.trim(), buyerKeyword.trim())
                    .stream().map(UserAccount::getId).collect(Collectors.toSet());
            if (buyerIds.isEmpty()) {
                spec = spec.and((root, q, cb) -> cb.disjunction());
            } else {
                spec = spec.and((root, q, cb) -> root.get("buyerId").in(buyerIds));
            }
        }
        if (sellerKeyword != null && !sellerKeyword.isBlank()) {
            Set<Long> sellerIds = userAccountRepository
                    .findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(sellerKeyword.trim(), sellerKeyword.trim())
                    .stream().map(UserAccount::getId).collect(Collectors.toSet());
            if (sellerIds.isEmpty()) {
                spec = spec.and((root, q, cb) -> cb.disjunction());
            } else {
                spec = spec.and((root, q, cb) -> root.get("sellerId").in(sellerIds));
            }
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

        return PageResults.from(page, pn, ps, o -> toDto(o, nameMap));
    }

    @Override
    public AdminOrderDTO getDetail(Long id) {
        OrderMain order = orderRepository.findById(id).orElse(null);
        if (order == null) return null;
        Map<Long, String> nameMap = loadUserNameMap(List.of(order));
        return toDto(order, nameMap);
    }

    @Override
    public void cancelOrder(Long id) {
        OrderMain order = orderRepository.findById(id).orElse(null);
        if (order == null) return;
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    @Override
    public void handleAfterSale(Long id) {
        OrderMain order = orderRepository.findById(id).orElse(null);
        if (order == null) return;
        order.setStatus("AFTER_SALE_HANDLED");
        orderRepository.save(order);
    }

    @Override
    public String export(String status, String buyerKeyword, String sellerKeyword,
                         String orderNo, String startTime, String endTime) {
        // 复用筛选逻辑（最多 5000 条）
        PageResult<AdminOrderDTO> page = pageList(status, buyerKeyword, sellerKeyword, orderNo, startTime, endTime, 1, 5000);
        List<AdminOrderDTO> list = page.getList() == null ? List.of() : page.getList();

        String filename = "admin-orders-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".csv";
        Path uploadDir = UploadPaths.getUploadDir();
        try {
            Files.createDirectories(uploadDir);
            Path file = uploadDir.resolve(filename);

            StringBuilder sb = new StringBuilder();
            sb.append("时间,订单号,商品,金额,买家,卖家,交易方式,状态").append("\n");
            for (AdminOrderDTO o : list) {
                sb.append(escape(o.getCreatedAt())).append(',')
                  .append(escape(o.getOrderNo())).append(',')
                  .append(escape(o.getProductTitle())).append(',')
                  .append(escape(o.getPriceText())).append(',')
                  .append(escape(o.getBuyerName())).append(',')
                  .append(escape(o.getSellerName())).append(',')
                  .append(escape(o.getDeliveryTypeText())).append(',')
                  .append(escape(o.getStatus()))
                  .append("\n");
            }
            byte[] bom = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
            byte[] out = new byte[bom.length + body.length];
            System.arraycopy(bom, 0, out, 0, bom.length);
            System.arraycopy(body, 0, out, bom.length, body.length);
            Files.write(file, out);
            return "/upload/" + filename;
        } catch (Exception e) {
            return null;
        }
    }

    private String escape(String v) {
        if (v == null) return "";
        String s = v.replace("\r", " ").replace("\n", " ");
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    private AdminOrderDTO toDto(OrderMain o, Map<Long, String> nameMap) {
        AdminOrderDTO dto = new AdminOrderDTO();
        dto.setId(o.getId());
        dto.setOrderNo(o.getOrderNo());
        dto.setProductTitle(o.getProductTitle());
        dto.setBuyerName(nameMap.getOrDefault(o.getBuyerId(), ""));
        dto.setSellerName(nameMap.getOrDefault(o.getSellerId(), ""));
        dto.setDeliveryTypeText(o.getDeliveryTypeText());
        dto.setStatus(o.getStatus());
        dto.setPriceText("￥" + (o.getTotalAmount() == null ? 0 : o.getTotalAmount()));
        dto.setCreatedAt(DateTimes.format(o.getCreatedAt()));
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

