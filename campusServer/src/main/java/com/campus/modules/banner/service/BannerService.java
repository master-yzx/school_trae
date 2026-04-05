package com.campus.modules.banner.service;

import com.campus.modules.banner.dto.BannerDTO;

import java.util.List;

public interface BannerService {

    List<BannerDTO> listEnabledBanners();
}

