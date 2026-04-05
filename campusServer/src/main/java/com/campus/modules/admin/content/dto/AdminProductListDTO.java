package com.campus.modules.admin.content.dto;

import lombok.Data;

@Data
public class AdminProductListDTO {
    private Long id;
    private String title;
    private String coverUrl;
    private Integer price;
    private String sellerName;
    private String campusName;
    private String status;
    private String statusText;
    private String createdAt;
    private String rejectReason;
}

