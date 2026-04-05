package com.campus.modules.systemconfig.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.systemconfig.dto.FooterConfigDTO;
import com.campus.modules.systemconfig.service.SystemConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/config")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    public SystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    @GetMapping("/footer")
    public ApiResponse<FooterConfigDTO> footer() {
        return ApiResponse.success(systemConfigService.getFooterConfig());
    }
}

