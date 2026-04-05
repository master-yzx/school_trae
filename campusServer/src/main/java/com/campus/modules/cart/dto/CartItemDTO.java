package com.campus.modules.cart.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String title;
    private String coverUrl;
    private String campusName;
    private String sellerName;
    private Integer price;
    private Integer quantity;
    private String deliveryTypeText;
}

