package com.campus.modules.campus.service.impl;

import com.campus.modules.campus.dto.CampusDTO;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.campus.service.CampusService;
import com.campus.modules.product.dto.RecommendProductDTO;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CampusServiceImpl implements CampusService {

    private final CampusRepository campusRepository;
    private final ProductRepository productRepository;
    private final UserAccountRepository userAccountRepository;

    public CampusServiceImpl(CampusRepository campusRepository,
                             ProductRepository productRepository,
                             UserAccountRepository userAccountRepository) {
        this.campusRepository = campusRepository;
        this.productRepository = productRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public List<CampusDTO> listAll() {
        List<Campus> campuses = campusRepository.findByEnabledTrueOrderByIdAsc();
        return campuses.stream().map(this::toDto).collect(Collectors.toList());
    }

    private CampusDTO toDto(Campus campus) {
        CampusDTO dto = new CampusDTO();
        dto.setId(campus.getId());
        dto.setName(campus.getName());
        return dto;
    }

    @Override
    public List<RecommendProductDTO> listHotProductsByCampus(Long campusId) {
        List<Product> products = productRepository.findTop10ByStatusAndCampusIdOrderByCreatedAtDesc("PUBLISHED", campusId);

        Set<Long> sellerIds = products.stream().map(Product::getSellerId).collect(Collectors.toSet());
        Map<Long, String> sellerNameMap = userAccountRepository.findAllById(sellerIds).stream()
                .collect(Collectors.toMap(UserAccount::getId,
                        u -> u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername(),
                        (a, b) -> a));

        String campusName = campusRepository.findById(campusId).map(Campus::getName).orElse("");

        return products.stream().map(p -> {
            RecommendProductDTO dto = new RecommendProductDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setCoverUrl(p.getCoverUrl());
            dto.setCampusName(campusName);
            dto.setSellerName(sellerNameMap.getOrDefault(p.getSellerId(), ""));
            dto.setPrice(p.getPrice());
            return dto;
        }).collect(Collectors.toList());
    }
}

