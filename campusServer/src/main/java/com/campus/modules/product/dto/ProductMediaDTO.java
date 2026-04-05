package com.campus.modules.product.dto;

import lombok.Data;

@Data
public class ProductMediaDTO {
    /**
     * 媒体类型：IMAGE / VIDEO
     */
    private String type;

    /**
     * 媒体地址
     */
    private String url;
}

