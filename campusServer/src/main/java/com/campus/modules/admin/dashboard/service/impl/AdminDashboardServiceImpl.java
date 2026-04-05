package com.campus.modules.admin.dashboard.service.impl;

import com.campus.modules.admin.dashboard.dto.OverviewCardDTO;
import com.campus.modules.admin.dashboard.service.AdminDashboardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Override
    public List<OverviewCardDTO> overviewCards() {
        List<OverviewCardDTO> list = new ArrayList<>();

        list.add(build("productTotal", "商品总数", 1280));
        list.add(build("pendingReview", "待审核商品", 12));
        list.add(build("soldTotal", "已卖出商品", 560));
        list.add(build("userTotal", "用户总数", 2300));
        list.add(build("sellerTotal", "卖家总数", 320));
        list.add(build("todayVisit", "今日访问量", 450));
        list.add(build("todayOrder", "今日订单数", 32));

        return list;
    }

    private OverviewCardDTO build(String key, String label, long value) {
        OverviewCardDTO dto = new OverviewCardDTO();
        dto.setKey(key);
        dto.setLabel(label);
        dto.setValue(value);
        return dto;
    }
}

