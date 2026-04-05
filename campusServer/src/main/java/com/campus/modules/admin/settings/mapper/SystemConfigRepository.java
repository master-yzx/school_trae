package com.campus.modules.admin.settings.mapper;

import com.campus.modules.admin.settings.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
}

