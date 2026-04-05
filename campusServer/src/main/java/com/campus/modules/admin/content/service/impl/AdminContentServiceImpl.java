package com.campus.modules.admin.content.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.admin.content.dto.AdminProductListDTO;
import com.campus.modules.admin.content.service.AdminContentService;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;

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
public class AdminContentServiceImpl implements AdminContentService {

    private final ProductRepository productRepository;
    private final CampusRepository campusRepository;
    private final UserAccountRepository userAccountRepository;

    public AdminContentServiceImpl(ProductRepository productRepository,
                                  CampusRepository campusRepository,
                                  UserAccountRepository userAccountRepository) {
        this.productRepository = productRepository;
        this.campusRepository = campusRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public PageResult<AdminProductListDTO> pageProducts(String status, String sellerKeyword, String titleKeyword, Long categoryId,
                                                        long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Specification<Product> spec = (root, q, cb) -> cb.conjunction();
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (titleKeyword != null && !titleKeyword.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(root.get("title"), "%" + titleKeyword.trim() + "%"));
        }
        if (categoryId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("categoryId"), categoryId));
        }
        if (sellerKeyword != null && !sellerKeyword.isBlank()) {
            List<UserAccount> sellers = userAccountRepository
                    .findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(sellerKeyword.trim(), sellerKeyword.trim());
            Set<Long> sellerIds = sellers.stream().map(UserAccount::getId).collect(Collectors.toSet());
            if (sellerIds.isEmpty()) {
                spec = spec.and((root, q, cb) -> cb.disjunction());
            } else {
                spec = spec.and((root, q, cb) -> root.get("sellerId").in(sellerIds));
            }
        }

        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps, Sort.by("createdAt").descending());
        Page<Product> page = productRepository.findAll(spec, pageable);

        Map<Long, String> campusNameMap = loadCampusNameMap(page.getContent());
        Map<Long, String> sellerNameMap = loadSellerNameMap(page.getContent());

        return PageResults.from(page, pn, ps, p -> toDto(p, campusNameMap, sellerNameMap));
    }

    @Override
    public void audit(Long id, boolean pass, String reason) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return;
        if (pass) {
            product.setStatus("PUBLISHED");
            product.setRejectReason(null);
        } else {
            product.setStatus("REJECTED");
            product.setRejectReason(reason);
        }
        productRepository.save(product);
    }

    @Override
    public void offline(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return;
        product.setStatus("OFFLINE");
        productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void batchOffline(Long[] ids) {
        if (ids == null || ids.length == 0) return;
        for (Long id : ids) {
            offline(id);
        }
    }

    @Override
    public void batchDelete(Long[] ids) {
        if (ids == null || ids.length == 0) return;
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public String getRejectReason(Long id) {
        return productRepository.findById(id).map(Product::getRejectReason).orElse(null);
    }

    private AdminProductListDTO toDto(Product p, Map<Long, String> campusNameMap, Map<Long, String> sellerNameMap) {
        AdminProductListDTO dto = new AdminProductListDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setCoverUrl(p.getCoverUrl());
        dto.setPrice(p.getPrice());
        dto.setSellerName(sellerNameMap.getOrDefault(p.getSellerId(), ""));
        dto.setCampusName(p.getCampusId() == null ? null : campusNameMap.getOrDefault(p.getCampusId(), ""));
        dto.setStatus(p.getStatus());
        dto.setStatusText(statusText(p.getStatus()));
        dto.setCreatedAt(DateTimes.format(p.getCreatedAt()));
        dto.setRejectReason(p.getRejectReason());
        return dto;
    }

    private String statusText(String status) {
        if (status == null) return "";
        return switch (status) {
            case "DRAFT" -> "草稿";
            case "PENDING" -> "待审核";
            case "PUBLISHED" -> "已发布";
            case "REJECTED" -> "已驳回";
            case "OFFLINE" -> "已下架";
            default -> status;
        };
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

