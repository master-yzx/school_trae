package com.campus.modules.forum.dto;

import lombok.Data;

@Data
public class FormPostListDTO {

    private Long id;

    private String title;

    private String summary;

    private String authorNickname;

    private String createdAt;

    private Long productId;

    private Long viewCount;

    private Long likeCount;

    private Long replyCount;
}

