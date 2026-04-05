package com.campus.modules.admin.stats.dto;

import lombok.Data;

@Data
public class AdminStatsOverviewDTO {

    private long todayOrderCount;

    private long sevenDayOrderCount;

    private long totalUserCount;

    private long totalSellerCount;

    private long totalProductCount;
}

