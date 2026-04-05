package com.campus.modules.product.dto;

import lombok.Data;

@Data
public class ProductSearchQuery {
    private String keyword;
    private Long sellerId;
    private Long categoryId;
    private Long campusId;
    private Integer minPrice;
    private Integer maxPrice;
    private String condition;      // 新旧程度
    private String deliveryType;   // 交易方式
    private Boolean freeShipping;
    private String sortField;
    private String sortOrder;
    private long pageNum = 1;
    private long pageSize = 20;
}

