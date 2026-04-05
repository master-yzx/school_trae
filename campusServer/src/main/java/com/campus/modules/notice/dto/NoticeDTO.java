package com.campus.modules.notice.dto;

import lombok.Data;

@Data
public class NoticeDTO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Boolean pinned;
    private Boolean enabled;
    private String createdAt;
}

