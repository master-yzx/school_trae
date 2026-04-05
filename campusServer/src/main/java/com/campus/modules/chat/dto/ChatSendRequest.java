package com.campus.modules.chat.dto;

import lombok.Data;

@Data
public class ChatSendRequest {

    private Long sessionId;

    private String content;
}

