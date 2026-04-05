package com.campus.modules.admin.stats.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.admin.stats.dto.AdminChartPointDTO;
import com.campus.modules.admin.stats.dto.AdminStatsOverviewDTO;
import com.campus.modules.admin.stats.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @GetMapping("/overview")
    public ApiResponse<AdminStatsOverviewDTO> overview(@RequestParam(required = false) String startTime,
                                                       @RequestParam(required = false) String endTime) {
        return ApiResponse.success(adminStatsService.getOverview(startTime, endTime));
    }

    @GetMapping("/orders")
    public ApiResponse<List<AdminChartPointDTO>> orderTrend(@RequestParam(required = false) String startTime,
                                                            @RequestParam(required = false) String endTime) {
        return ApiResponse.success(adminStatsService.listOrderTrend(startTime, endTime));
    }

    @GetMapping("/users")
    public ApiResponse<List<AdminChartPointDTO>> userGrowth(@RequestParam(required = false) String startTime,
                                                            @RequestParam(required = false) String endTime) {
        return ApiResponse.success(adminStatsService.listUserGrowth(startTime, endTime));
    }

    @GetMapping("/categories")
    public ApiResponse<List<AdminChartPointDTO>> categoryDistribution(@RequestParam(required = false) String startTime,
                                                                      @RequestParam(required = false) String endTime) {
        return ApiResponse.success(adminStatsService.listCategoryDistribution(startTime, endTime));
    }

    @PostMapping("/export")
    public ApiResponse<String> export(@RequestParam(required = false) String startTime,
                                      @RequestParam(required = false) String endTime) {
        String url = adminStatsService.export(startTime, endTime);
        return ApiResponse.success(url);
    }
}

