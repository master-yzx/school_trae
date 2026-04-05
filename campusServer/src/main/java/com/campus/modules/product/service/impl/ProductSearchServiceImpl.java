package com.campus.modules.product.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.product.dto.ProductListItemDTO;
import com.campus.modules.product.dto.ProductSearchQuery;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.product.service.ProductSearchService;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import com.campus.modules.category.entity.Category;
import com.campus.modules.category.mapper.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductRepository productRepository;
    private final CampusRepository campusRepository;
    private final UserAccountRepository userAccountRepository;
    private final CategoryRepository categoryRepository;

    public ProductSearchServiceImpl(ProductRepository productRepository,
                                   CampusRepository campusRepository,
                                   UserAccountRepository userAccountRepository,
                                   CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.campusRepository = campusRepository;
        this.userAccountRepository = userAccountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public PageResult<ProductListItemDTO> search(ProductSearchQuery query) {
        Specification<Product> spec = (root, q, cb) -> cb.equal(root.get("status"), "PUBLISHED");

        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String like = "%" + query.getKeyword().trim() + "%";
            spec = spec.and((root, q, cb) -> cb.like(root.get("title"), like));
        }
        if (query.getSellerId() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("sellerId"), query.getSellerId()));
        }
        if (query.getCategoryId() != null) {
            java.util.Set<Long> categoryIds = resolveCategoryIdsWithChildren(query.getCategoryId());
            if (categoryIds.isEmpty()) {
                // 无匹配分类时直接返回空结果
                return PageResults.empty(query.getPageNum(), query.getPageSize());
            }
            spec = spec.and((root, q, cb) -> root.get("categoryId").in(categoryIds));
        }
        if (query.getCampusId() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("campusId"), query.getCampusId()));
        }
        if (query.getMinPrice() != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("price"), query.getMinPrice()));
        }
        if (query.getMaxPrice() != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("price"), query.getMaxPrice()));
        }
        if (query.getCondition() != null && !query.getCondition().isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("conditionText"), query.getCondition()));
        }
        if (query.getDeliveryType() != null && !query.getDeliveryType().isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(root.get("deliveryTypeText"), "%" + query.getDeliveryType() + "%"));
        }
        if (query.getFreeShipping() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("freeShipping"), query.getFreeShipping()));
        }

        Sort sort = resolveSort(query);
        long pageNum = Math.max(1, query.getPageNum());
        long pageSize = Math.max(1, query.getPageSize());
        Pageable pageable = PageRequest.of((int) (pageNum - 1), (int) pageSize, sort);

        Page<Product> page = productRepository.findAll(spec, pageable);

        Map<Long, String> campusNameMap = loadCampusNameMap(page.getContent());
        Map<Long, String> sellerNameMap = loadSellerNameMap(page.getContent());

        return PageResults.from(page, pageNum, pageSize, p -> toListItem(p, campusNameMap, sellerNameMap));
    }

    private java.util.Set<Long> resolveCategoryIdsWithChildren(Long rootCategoryId) {
        java.util.List<Category> all = categoryRepository.findAll();
        java.util.Set<Long> result = new java.util.HashSet<>();
        collectCategoryIds(all, rootCategoryId, result);
        return result;
    }

    private void collectCategoryIds(java.util.List<Category> all, Long id, java.util.Set<Long> acc) {
        if (id == null) return;
        acc.add(id);
        for (Category c : all) {
            if (id.equals(c.getParentId())) {
                collectCategoryIds(all, c.getId(), acc);
            }
        }
    }

    private Sort resolveSort(ProductSearchQuery query) {
        String field = query.getSortField();
        String order = query.getSortOrder();
        boolean desc = order == null || order.isBlank() || "desc".equalsIgnoreCase(order);

        String mapped;
        if ("price".equalsIgnoreCase(field)) {
            mapped = "price";
        } else if ("createdAt".equalsIgnoreCase(field) || "created_at".equalsIgnoreCase(field)) {
            mapped = "createdAt";
        } else {
            mapped = "createdAt";
        }
        return desc ? Sort.by(mapped).descending() : Sort.by(mapped).ascending();
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

    private ProductListItemDTO toListItem(Product p, Map<Long, String> campusNameMap, Map<Long, String> sellerNameMap) {
        ProductListItemDTO dto = new ProductListItemDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setCoverUrl(p.getCoverUrl());
        dto.setCampusName(p.getCampusId() == null ? null : campusNameMap.getOrDefault(p.getCampusId(), ""));
        dto.setSellerName(sellerNameMap.getOrDefault(p.getSellerId(), ""));
        dto.setPrice(p.getPrice());
        dto.setConditionText(p.getConditionText());
        dto.setDeliveryTypeText(p.getDeliveryTypeText());
        dto.setFreeShipping(p.getFreeShipping());
        dto.setCreatedAt(DateTimes.format(p.getCreatedAt()));
        return dto;
    }
}

