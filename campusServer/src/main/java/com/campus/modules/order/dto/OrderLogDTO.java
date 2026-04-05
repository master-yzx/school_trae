package com.campus.modules.order.dto;

import lombok.Data;

@Data
public class OrderLogDTO {
    private String status;
    private String message;
    private String createdAt;
}

