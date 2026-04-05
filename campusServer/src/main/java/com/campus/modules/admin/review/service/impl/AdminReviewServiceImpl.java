package com.campus.modules.admin.review.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.admin.review.dto.AdminReviewProductDTO;
import com.campus.modules.admin.review.service.AdminReviewService;
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
public class AdminReviewServiceImpl implements AdminReviewService {

    private final ProductRepository productRepository;
    private final CampusRepository campusRepository;
    private final UserAccountRepository userAccountRepository;

    public AdminReviewServiceImpl(ProductRepository productRepository,
                                 CampusRepository campusRepository,
                                 UserAccountRepository userAccountRepository) {
        this.productRepository = productRepository;
        this.campusRepository = campusRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public PageResult<AdminReviewProductDTO> pageReviewProducts(String status, String sellerKeyword, String titleKeyword, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        String effectiveStatus = (status == null || status.isBlank()) ? "PENDING" : status;

        Specification<Product> spec = (root, q, cb) -> cb.equal(root.get("status"), effectiveStatus);

        if (titleKeyword != null && !titleKeyword.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(root.get("title"), "%" + titleKeyword.trim() + "%"));
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
    public void approve(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return;
        product.setStatus("PUBLISHED");
        product.setRejectReason(null);
        productRepository.save(product);
    }

    @Override
    public void reject(Long id, String reason) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return;
        product.setStatus("REJECTED");
        product.setRejectReason(reason);
        productRepository.save(product);
    }

    private AdminReviewProductDTO toDto(Product p, Map<Long, String> campusNameMap, Map<Long, String> sellerNameMap) {
        AdminReviewProductDTO dto = new AdminReviewProductDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setCoverUrl(p.getCoverUrl());
        dto.setPrice(p.getPrice());
        dto.setSellerName(sellerNameMap.getOrDefault(p.getSellerId(), ""));
        dto.setCampusName(p.getCampusId() == null ? null : campusNameMap.getOrDefault(p.getCampusId(), ""));
        dto.setStatus(p.getStatus());
        dto.setStatusText("PENDING".equals(p.getStatus()) ? "待审核" : p.getStatus());
        dto.setSubmittedAt(DateTimes.format(p.getCreatedAt()));
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

