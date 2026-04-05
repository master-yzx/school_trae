package com.campus.modules.product.dto;

import lombok.Data;

@Data
public class RecommendProductDTO {
    private Long id;
    private String title;
    private String coverUrl;
    private String campusName;
    private String sellerName;
    private Integer price;
}

