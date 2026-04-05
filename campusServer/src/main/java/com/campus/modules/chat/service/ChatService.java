package com.campus.modules.chat.service;

import com.campus.common.result.PageResult;
import com.campus.modules.chat.dto.ChatMessageDTO;
import com.campus.modules.chat.dto.ChatSessionDTO;
import com.campus.modules.chat.dto.ChatSessionDetailDTO;

public interface ChatService {

    PageResult<ChatSessionDTO> pageMySessions(long pageNum, long pageSize);

    ChatSessionDetailDTO getMySession(Long sessionId);

    Long startByProduct(Long productId);

    Long startByOrder(Long orderId);

    ChatMessageDTO appendMessage(Long sessionId, Long fromUserId, String content);
}

