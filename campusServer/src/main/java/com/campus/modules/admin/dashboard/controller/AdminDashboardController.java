package com.campus.modules.admin.dashboard.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.admin.dashboard.dto.OverviewCardDTO;
import com.campus.modules.admin.dashboard.service.AdminDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/overview")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/cards")
    public ApiResponse<List<OverviewCardDTO>> cards() {
        return ApiResponse.success(adminDashboardService.overviewCards());
    }

    @PostMapping("/cache/clear")
    public ApiResponse<Void> clearCache() {
        // TODO: 清空 Redis 缓存
        return ApiResponse.success();
    }
}

