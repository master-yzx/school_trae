package com.campus.modules.product.dto;

import lombok.Data;

@Data
public class SellerProductListItemDTO {
    private Long id;
    private String title;
    private String coverUrl;
    private Integer price;
    private String conditionText;
    private String status;
    private String createdAt;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer consultCount;
}

