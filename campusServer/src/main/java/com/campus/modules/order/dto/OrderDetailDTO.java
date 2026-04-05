package com.campus.modules.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDetailDTO {
    private Long id;
    private String orderNo;
    private String role;
    private String status;
    private String createdAt;

    private String productTitle;
    private String productCoverUrl;
    private Integer price;
    private Integer quantity;
    private Integer totalAmount;

    private String buyerName;
    private String sellerName;
    private String deliveryTypeText;
    private String address;
    private String logisticsNo;

    private List<OrderItemDTO> items;

    private List<OrderLogDTO> logs;
}

