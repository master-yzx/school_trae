package com.campus.modules.notice.dto;

import lombok.Data;

@Data
public class NoticeSaveRequest {
    private Long id;
    private String title;
    private String content;
    private String type;   // SYSTEM / ACTIVITY / RULE
    private Boolean pinned;
    private Boolean enabled;
}

