package com.campus.modules.chat.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {

    private Long id;

    private Long sessionId;

    private Long fromUserId;

    private Long toUserId;

    private String content;

    private String createdAt;
}

