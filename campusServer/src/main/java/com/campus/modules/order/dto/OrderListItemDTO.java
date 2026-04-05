package com.campus.modules.order.dto;

import lombok.Data;

@Data
public class OrderListItemDTO {
    private Long id;
    private String orderNo;
    private String role; // USER or SELLER
    private String productTitle;
    private String productCoverUrl;
    private Integer totalAmount;
    private String otherPartyName; // 卖家/买家
    private String deliveryTypeText;
    private String status;
    private String createdAt;
}

