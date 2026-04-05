package com.campus.modules.product.dto;

import lombok.Data;

@Data
public class ProductPublishRequest {
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
    /** 前端上传得到的封面图地址（/upload/xxx.jpg） */
    private String coverUrl;
}

