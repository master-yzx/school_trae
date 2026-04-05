package com.campus.modules.cart.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.PageResults;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.cart.dto.CartItemDTO;
import com.campus.modules.cart.entity.CartItem;
import com.campus.modules.cart.mapper.CartItemRepository;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.cart.service.CartService;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CampusRepository campusRepository;
    private final UserAccountRepository userAccountRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           CampusRepository campusRepository,
                           UserAccountRepository userAccountRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.campusRepository = campusRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public PageResult<CartItemDTO> pageCart(String keyword, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            return PageResults.empty(pageNum, pageSize);
        }

        Specification<CartItem> spec = (root, q, cb) -> cb.equal(root.get("userId"), currentUserId);

        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            Set<Long> productIds = productRepository.findAll((r, qq, cb) -> cb.like(r.get("title"), like))
                    .stream().map(Product::getId).collect(Collectors.toSet());
            if (productIds.isEmpty()) {
                spec = spec.and((root, q, cb) -> cb.disjunction());
            } else {
                spec = spec.and((root, q, cb) -> root.get("productId").in(productIds));
            }
        }

        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps, Sort.by("createdAt").descending());
        Page<CartItem> page = cartItemRepository.findAll(spec, pageable);

        Map<Long, Product> productMap = productRepository.findAllById(
                page.getContent().stream().map(CartItem::getProductId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));

        Map<Long, String> campusNameMap = loadCampusNameMap(productMap.values().stream().toList());
        Map<Long, String> sellerNameMap = loadSellerNameMap(productMap.values().stream().toList());

        return PageResults.from(page, pn, ps, ci -> toDto(ci, productMap, campusNameMap, sellerNameMap));
    }

    @Override
    public void addItem(Long productId, Integer quantity) {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null || productId == null) return;
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;
        int qty = quantity == null || quantity < 1 ? 1 : quantity;
        CartItem existing = cartItemRepository.findByUserIdAndProductId(currentUserId, productId).orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qty);
            cartItemRepository.save(existing);
        } else {
            CartItem item = new CartItem();
            item.setUserId(currentUserId);
            item.setProductId(productId);
            item.setQuantity(qty);
            item.setCreatedAt(LocalDateTime.now());
            cartItemRepository.save(item);
        }
    }

    @Override
    public void clearCart() {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "请先登录");
        }
        cartItemRepository.deleteByUserId(currentUserId);
    }

    @Override
    public void deleteItem(Long id) {
        cartItemRepository.deleteById(id);
    }

    @Override
    public void updateQuantity(Long id, Integer quantity) {
        CartItem item = cartItemRepository.findById(id).orElse(null);
        if (item == null) return;
        item.setQuantity(quantity == null || quantity < 1 ? 1 : quantity);
        cartItemRepository.save(item);
    }

    private CartItemDTO toDto(CartItem cartItem,
                              Map<Long, Product> productMap,
                              Map<Long, String> campusNameMap,
                              Map<Long, String> sellerNameMap) {
        Product p = productMap.get(cartItem.getProductId());

        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProductId());
        dto.setQuantity(cartItem.getQuantity());

        if (p != null) {
            dto.setTitle(p.getTitle());
            dto.setCoverUrl(p.getCoverUrl());
            dto.setCampusName(p.getCampusId() == null ? null : campusNameMap.getOrDefault(p.getCampusId(), ""));
            dto.setSellerName(sellerNameMap.getOrDefault(p.getSellerId(), ""));
            dto.setPrice(p.getPrice());
            dto.setDeliveryTypeText(p.getDeliveryTypeText());
        }
        return dto;
    }

    private Map<Long, String> loadCampusNameMap(List<Product> products) {
        Set<Long> campusIds = products.stream()
                .map(Product::getCampusId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        if (campusIds.isEmpty()) return Map.of();

        return campusRepository.findAllById(campusIds).stream()
                .collect(Collectors.toMap(Campus::getId, Campus::getName, (a, b) -> a));
    }

    private Map<Long, String> loadSellerNameMap(List<Product> products) {
        Set<Long> sellerIds = products.stream()
                .map(Product::getSellerId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        if (sellerIds.isEmpty()) return Map.of();

        return userAccountRepository.findAllById(sellerIds).stream()
                .collect(Collectors.toMap(UserAccount::getId,
                        u -> u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername(),
                        (a, b) -> a));
    }
}

