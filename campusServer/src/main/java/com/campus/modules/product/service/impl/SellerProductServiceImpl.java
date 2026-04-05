package com.campus.modules.product.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.product.dto.ProductPublishRequest;
import com.campus.modules.product.dto.SellerProductDetailDTO;
import com.campus.modules.product.dto.SellerProductListItemDTO;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.product.service.SellerProductService;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SellerProductServiceImpl implements SellerProductService {

    private final ProductRepository productRepository;
    private final CampusRepository campusRepository;

    public SellerProductServiceImpl(ProductRepository productRepository, CampusRepository campusRepository) {
        this.productRepository = productRepository;
        this.campusRepository = campusRepository;
    }

    @Override
    public PageResult<SellerProductListItemDTO> pageSellerProducts(String status, String keyword, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            return PageResults.empty(pageNum, pageSize);
        }

        Specification<Product> spec = (root, q, cb) -> cb.equal(root.get("sellerId"), currentUserId);
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            spec = spec.and((root, q, cb) -> cb.like(root.get("title"), like));
        }

        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps, Sort.by("createdAt").descending());
        Page<Product> page = productRepository.findAll(spec, pageable);

        return PageResults.from(page, pn, ps, this::toDto);
    }

    @Override
    public Long saveDraft(ProductPublishRequest request) {
        Product product = upsertFromRequest(request);
        product.setStatus("DRAFT");
        productRepository.save(product);
        return product.getId();
    }

    @Override
    public Long submitForReview(ProductPublishRequest request) {
        Product product = upsertFromRequest(request);
        product.setStatus("PENDING");
        product.setRejectReason(null);
        productRepository.save(product);
        return product.getId();
    }

    @Override
    public SellerProductDetailDTO getDetail(Long id) {
        Product p = productRepository.findById(id).orElse(null);
        if (p == null) return null;
        SellerProductDetailDTO dto = new SellerProductDetailDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setCategoryId(p.getCategoryId());
        dto.setPrice(p.getPrice());
        dto.setCondition(p.getConditionText());
        dto.setDeliveryType(p.getDeliveryTypeText());
        dto.setFreeShipping(p.getFreeShipping());
        dto.setPickupLocation(p.getPickupLocation());
        dto.setDescriptionHtml(p.getDescriptionHtml());
        dto.setCoverUrl(p.getCoverUrl());

        if (p.getCampusId() != null) {
            campusRepository.findById(p.getCampusId())
                    .map(Campus::getName)
                    .ifPresent(dto::setCampusName);
        }
        return dto;
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void changeStatus(Long id, String status) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return;
        product.setStatus(status);
        productRepository.save(product);
    }

    private SellerProductListItemDTO toDto(Product p) {
        SellerProductListItemDTO dto = new SellerProductListItemDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setCoverUrl(p.getCoverUrl());
        dto.setPrice(p.getPrice());
        dto.setConditionText(p.getConditionText());
        dto.setStatus(p.getStatus());
        dto.setCreatedAt(DateTimes.format(p.getCreatedAt()));
        dto.setViewCount(p.getViewCount() == null ? 0 : p.getViewCount());
        dto.setFavoriteCount(p.getFavoriteCount() == null ? 0 : p.getFavoriteCount());
        dto.setConsultCount(p.getConsultCount() == null ? 0 : p.getConsultCount());
        return dto;
    }

    private Product upsertFromRequest(ProductPublishRequest request) {
        Product product = request.getId() == null ? new Product() : productRepository.findById(request.getId()).orElse(new Product());

        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            throw new IllegalStateException("未登录卖家无法发布商品");
        }
        product.setSellerId(currentUserId);
        product.setTitle(request.getTitle());
        product.setCategoryId(request.getCategoryId());
        product.setPrice(request.getPrice());
        product.setConditionText(request.getCondition());
        product.setDeliveryTypeText(request.getDeliveryType());
        product.setFreeShipping(request.getFreeShipping() != null ? request.getFreeShipping() : Boolean.FALSE);
        product.setPickupLocation(request.getPickupLocation());
        product.setDescriptionHtml(request.getDescriptionHtml());
        product.setCoverUrl(request.getCoverUrl());

        Long campusId = resolveCampusId(request.getCampusName());
        product.setCampusId(campusId);

        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }
        if (product.getViewCount() == null) product.setViewCount(0);
        if (product.getFavoriteCount() == null) product.setFavoriteCount(0);
        if (product.getConsultCount() == null) product.setConsultCount(0);

        return product;
    }

    private Long resolveCampusId(String campusName) {
        if (campusName == null || campusName.isBlank()) return null;
        Optional<Campus> campus = campusRepository.findFirstByName(campusName.trim());
        return campus.map(Campus::getId).orElse(null);
    }
}

