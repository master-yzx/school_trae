package com.campus.modules.message.service;

import com.campus.common.result.PageResult;
import com.campus.modules.message.dto.MessageDTO;

public interface MessageService {

    PageResult<MessageDTO> pageMessages(String type, Boolean read, long pageNum, long pageSize);

    void markRead(Long id);

    void deleteOne(Long id);

    void batchMarkRead();

    void batchDeleteRead();

    /**
     * 给指定用户发一条站内消息（用于订单/审核/系统通知等）
     */
    void sendToUser(Long userId, String type, String title, String content);

    /**
     * 将公告推送给所有用户，写入消息中心（type=SYSTEM）
     */
    void sendNoticeToAllUsers(String title, String content);
}

