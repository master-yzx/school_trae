package com.campus.modules.message.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private Long id;
    private String type; // AUDIT / ORDER / CHAT / SYSTEM
    private String title;
    private String content;
    private String createdAt;
    private Boolean read;
}

