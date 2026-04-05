package com.campus.modules.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailDTO {
    private Long id;
    private String title;
    private String categoryName;
    private Integer price;
    private String conditionText;
    private String deliveryTypeText;
    private Boolean freeShipping;
    private String campusName;
    private String pickupLocation;
    private String descriptionHtml;

    private Long sellerId;
    private String sellerName;
    private String sellerCampusName;
    private Integer sellerProductCount;

    private List<ProductMediaDTO> mediaList;
}

