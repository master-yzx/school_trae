package com.campus.modules.admin.seller.dto;

import lombok.Data;

@Data
public class AdminSellerDTO {
    private Long id;
    private String nickname;
    private String studentNo;
    private String phone;
    private String campusName;
    private String registerTime;
    private Integer productCount;
    private String status; // NORMAL / DISABLED
}

