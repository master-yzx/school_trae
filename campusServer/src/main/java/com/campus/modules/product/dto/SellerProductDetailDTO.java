package com.campus.modules.product.dto;

import lombok.Data;

@Data
public class SellerProductDetailDTO {

    private Long id;
    private String title;
    private Long categoryId;
    private Integer price;
    private String condition;
    private String deliveryType;
    private Boolean freeShipping;
    private String campusName;
    private String pickupLocation;
    private String descriptionHtml;
    private String coverUrl;
}

