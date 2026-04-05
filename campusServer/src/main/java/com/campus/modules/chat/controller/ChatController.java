package com.campus.modules.chat.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.chat.dto.ChatSessionDTO;
import com.campus.modules.chat.dto.ChatSessionDetailDTO;
import com.campus.modules.chat.dto.ChatStartRequest;
import com.campus.modules.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import com.campus.modules.chat.dto.ChatSendRequest;
import com.campus.modules.chat.dto.ChatMessageDTO;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/sessions")
    public ApiResponse<PageResult<ChatSessionDTO>> sessions(@RequestParam(defaultValue = "1") long pageNum,
                                                            @RequestParam(defaultValue = "20") long pageSize) {
        return ApiResponse.success(chatService.pageMySessions(pageNum, pageSize));
    }

    @GetMapping("/sessions/{id}")
    public ApiResponse<ChatSessionDetailDTO> sessionDetail(@PathVariable Long id) {
        return ApiResponse.success(chatService.getMySession(id));
    }

    @PostMapping("/start")
    public ApiResponse<Long> start(@RequestBody ChatStartRequest request) {
        Long sessionId = null;
        if (request.getOrderId() != null) {
            sessionId = chatService.startByOrder(request.getOrderId());
        } else if (request.getProductId() != null) {
            sessionId = chatService.startByProduct(request.getProductId());
        }
        return ApiResponse.success(sessionId);
    }

    @MessageMapping("/chat.send")
    public void handleChat(@Payload ChatSendRequest payload, Principal principal) {
        if (principal == null || payload == null || payload.getSessionId() == null) return;
        Long fromUserId;
        try {
            fromUserId = Long.parseLong(principal.getName());
        } catch (NumberFormatException ex) {
            return;
        }
        ChatMessageDTO msg = chatService.appendMessage(payload.getSessionId(), fromUserId, payload.getContent());
        if (msg == null) return;
        // 发送给双方
        String destFrom = "/queue/chat/" + msg.getFromUserId();
        String destTo = "/queue/chat/" + msg.getToUserId();
        messagingTemplate.convertAndSend(destFrom, msg);
        if (!destTo.equals(destFrom)) {
            messagingTemplate.convertAndSend(destTo, msg);
        }
    }
}

