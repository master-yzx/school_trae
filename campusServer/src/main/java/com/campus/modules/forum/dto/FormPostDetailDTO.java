package com.campus.modules.forum.dto;

import lombok.Data;

@Data
public class FormPostDetailDTO {

    private Long id;

    private String title;

    private String content;

    private String authorNickname;

    private String createdAt;

    private Long viewCount;

    private Long likeCount;

    private Long replyCount;

    private Long productId;
}

