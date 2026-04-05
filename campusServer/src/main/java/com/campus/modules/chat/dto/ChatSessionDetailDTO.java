package com.campus.modules.chat.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatSessionDetailDTO {

    private ChatSessionDTO session;

    private List<ChatMessageDTO> messages;
}

