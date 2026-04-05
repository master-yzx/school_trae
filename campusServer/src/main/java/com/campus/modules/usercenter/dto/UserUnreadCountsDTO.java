package com.campus.modules.usercenter.dto;

import lombok.Data;

@Data
public class UserUnreadCountsDTO {
    /**
     * 站内消息中心未读数（/api/messages）
     */
    private int messageUnread;

    /**
     * 聊天中心未读数（/api/chat/sessions 中各会话 unreadCount 的总和）
     */
    private int chatUnread;
}

