package com.campus.modules.banner.mapper;

import com.campus.modules.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findByEnabledTrueOrderBySortOrderAsc();
}

