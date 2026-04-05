package com.campus.modules.chat.dto;

import lombok.Data;

@Data
public class ChatSessionDTO {

    private Long id;

    private Long productId;

    private String productTitle;

    private String productCoverUrl;

    private Long otherUserId;

    private String otherNickname;

    private String lastMessage;

    private String lastTime;

    private Integer unreadCount;
}

