package com.campus.modules.product.service.impl;

import com.campus.common.utils.DateTimes;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.category.entity.Category;
import com.campus.modules.category.mapper.CategoryRepository;
import com.campus.modules.product.dto.ProductDetailDTO;
import com.campus.modules.product.dto.ProductMediaDTO;
import com.campus.modules.product.dto.RecommendProductDTO;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.entity.ProductMedia;
import com.campus.modules.product.mapper.ProductMediaRepository;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.product.service.ProductDetailService;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductRepository productRepository;
    private final ProductMediaRepository productMediaRepository;
    private final CampusRepository campusRepository;
    private final CategoryRepository categoryRepository;
    private final UserAccountRepository userAccountRepository;

    public ProductDetailServiceImpl(ProductRepository productRepository,
                                   ProductMediaRepository productMediaRepository,
                                   CampusRepository campusRepository,
                                   CategoryRepository categoryRepository,
                                   UserAccountRepository userAccountRepository) {
        this.productRepository = productRepository;
        this.productMediaRepository = productMediaRepository;
        this.campusRepository = campusRepository;
        this.categoryRepository = categoryRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public ProductDetailDTO getDetail(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return null;

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setCategoryName(resolveCategoryName(product.getCategoryId()));
        dto.setPrice(product.getPrice());
        dto.setConditionText(product.getConditionText());
        dto.setDeliveryTypeText(product.getDeliveryTypeText());
        dto.setFreeShipping(product.getFreeShipping());
        dto.setCampusName(resolveCampusName(product.getCampusId()));
        dto.setPickupLocation(product.getPickupLocation());
        dto.setDescriptionHtml(product.getDescriptionHtml());

        dto.setSellerId(product.getSellerId());
        dto.setSellerName(resolveSellerName(product.getSellerId()));
        dto.setSellerCampusName(resolveSellerCampusName(product.getSellerId()));
        dto.setSellerProductCount((int) productRepository.countBySellerId(product.getSellerId()));

        // 详情媒体优先使用媒体表；如果没有配置媒体记录，则回退使用封面图
        List<ProductMediaDTO> medias = listMedia(productId);
        if ((medias == null || medias.isEmpty())
                && product.getCoverUrl() != null
                && !product.getCoverUrl().isBlank()) {
            ProductMediaDTO cover = new ProductMediaDTO();
            cover.setType("IMAGE");
            cover.setUrl(product.getCoverUrl());
            medias = List.of(cover);
        }
        dto.setMediaList(medias);
        return dto;
    }

    @Override
    public List<ProductMediaDTO> listMedia(Long productId) {
        List<ProductMedia> medias = productMediaRepository.findByProductIdOrderBySortOrderAsc(productId);
        return medias.stream().map(m -> {
            ProductMediaDTO dto = new ProductMediaDTO();
            dto.setType(m.getType());
            dto.setUrl(m.getUrl());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RecommendProductDTO> listRelated(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getCategoryId() == null) return Collections.emptyList();

        List<Product> related = productRepository.findTop6ByStatusAndCategoryIdAndIdNotOrderByCreatedAtDesc(
                "PUBLISHED",
                product.getCategoryId(),
                productId
        );

        Map<Long, String> campusNameMap = loadCampusNameMap(related);
        Map<Long, String> sellerNameMap = loadSellerNameMap(related);

        return related.stream().map(p -> {
            RecommendProductDTO dto = new RecommendProductDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setCoverUrl(p.getCoverUrl());
            dto.setCampusName(p.getCampusId() == null ? null : campusNameMap.getOrDefault(p.getCampusId(), ""));
            dto.setSellerName(sellerNameMap.getOrDefault(p.getSellerId(), ""));
            dto.setPrice(p.getPrice());
            return dto;
        }).collect(Collectors.toList());
    }

    private String resolveCampusName(Long campusId) {
        if (campusId == null) return null;
        return campusRepository.findById(campusId).map(Campus::getName).orElse("");
    }

    private String resolveCategoryName(Long categoryId) {
        if (categoryId == null) return null;
        Category c = categoryRepository.findById(categoryId).orElse(null);
        if (c == null) return "";
        if (c.getParentId() == null) return c.getName();
        Category parent = categoryRepository.findById(c.getParentId()).orElse(null);
        return parent == null ? c.getName() : parent.getName() + " / " + c.getName();
    }

    private String resolveSellerName(Long sellerId) {
        if (sellerId == null) return null;
        return userAccountRepository.findById(sellerId)
                .map(u -> u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername())
                .orElse("");
    }

    private String resolveSellerCampusName(Long sellerId) {
        if (sellerId == null) return null;
        UserAccount u = userAccountRepository.findById(sellerId).orElse(null);
        if (u == null || u.getCampusId() == null) return "";
        return resolveCampusName(u.getCampusId());
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

