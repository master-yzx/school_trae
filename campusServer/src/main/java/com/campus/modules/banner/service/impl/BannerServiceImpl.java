package com.campus.modules.banner.service.impl;

import com.campus.modules.banner.dto.BannerDTO;
import com.campus.modules.banner.entity.Banner;
import com.campus.modules.banner.mapper.BannerRepository;
import com.campus.modules.banner.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    public BannerServiceImpl(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Override
    public List<BannerDTO> listEnabledBanners() {
        List<Banner> banners = bannerRepository.findByEnabledTrueOrderBySortOrderAsc();
        return banners.stream().map(this::toDto).collect(Collectors.toList());
    }

    private BannerDTO toDto(Banner entity) {
        BannerDTO dto = new BannerDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setSubtitle(entity.getSubtitle());
        dto.setImageUrl(entity.getImageUrl());
        return dto;
    }
}

