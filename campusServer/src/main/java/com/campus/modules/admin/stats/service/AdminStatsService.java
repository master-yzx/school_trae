package com.campus.modules.admin.stats.service;

import com.campus.modules.admin.stats.dto.AdminChartPointDTO;
import com.campus.modules.admin.stats.dto.AdminStatsOverviewDTO;

import java.util.List;

public interface AdminStatsService {

    AdminStatsOverviewDTO getOverview(String startTime, String endTime);

    List<AdminChartPointDTO> listOrderTrend(String startTime, String endTime);

    List<AdminChartPointDTO> listUserGrowth(String startTime, String endTime);

    List<AdminChartPointDTO> listCategoryDistribution(String startTime, String endTime);

    String export(String startTime, String endTime);
}

