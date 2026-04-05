package com.campus.modules.admin.order.dto;

import lombok.Data;

@Data
public class AdminOrderDTO {

    private Long id;

    private String orderNo;

    private String productTitle;

    private String buyerName;

    private String sellerName;

    private String deliveryTypeText;

    private String status;

    private String priceText;

    private String createdAt;
}

