package com.campus.modules.order.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long productId;
    private String productTitle;
    private String productCoverUrl;
    private Integer price;
    private Integer quantity;
    private Integer totalAmount;

    // 为了前端“每个商品后面展示买家/卖家信息”，直接附带（同一订单内通常一致）
    private String buyerName;
    private String sellerName;
    private String deliveryTypeText;
}

