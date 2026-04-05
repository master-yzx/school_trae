package com.campus.modules.admin.log.dto;

import lombok.Data;

@Data
public class AdminLogDTO {

    private Long id;

    private String operator;

    private String type;

    private String content;

    private String ip;

    private String result;

    private String createdAt;
}

