package com.campus.modules.forum.dto;

import lombok.Data;

@Data
public class FormCommentDTO {

    private Long id;

    private String content;

    private String authorNickname;

    private String createdAt;
}

