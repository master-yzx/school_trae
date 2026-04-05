package com.campus.modules.banner.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.banner.dto.BannerDTO;
import com.campus.modules.banner.service.BannerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    public ApiResponse<List<BannerDTO>> list() {
        return ApiResponse.success(bannerService.listEnabledBanners());
    }
}

