package com.campus.modules.product.service.impl;

import com.campus.modules.product.dto.RecommendProductDTO;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.product.service.ProductRecommendService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;

@Service
public class ProductRecommendServiceImpl implements ProductRecommendService {

    private final ProductRepository productRepository;
    private final CampusRepository campusRepository;
    private final UserAccountRepository userAccountRepository;

    public ProductRecommendServiceImpl(ProductRepository productRepository,
                                      CampusRepository campusRepository,
                                      UserAccountRepository userAccountRepository) {
        this.productRepository = productRepository;
        this.campusRepository = campusRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public List<RecommendProductDTO> listRecommendProducts() {
        List<Product> products = productRepository.findTop12ByStatusOrderByCreatedAtDesc("PUBLISHED");

        Map<Long, String> campusNameMap = loadCampusNameMap(products);
        Map<Long, String> sellerNameMap = loadSellerNameMap(products);

        return products.stream().map(p -> {
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

