package com.campus.modules.product.dto;

import lombok.Data;

@Data
public class ProductListItemDTO {
    private Long id;
    private String title;
    private String coverUrl;
    private String campusName;
    private String sellerName;
    private Integer price;
    private String conditionText;
    private String deliveryTypeText;
    private Boolean freeShipping;
    private String createdAt;
}

