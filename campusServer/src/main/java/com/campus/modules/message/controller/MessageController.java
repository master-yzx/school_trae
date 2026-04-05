package com.campus.modules.message.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.message.dto.MessageDTO;
import com.campus.modules.message.service.MessageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ApiResponse<PageResult<MessageDTO>> page(@RequestParam(required = false) String type,
                                                    @RequestParam(required = false) Boolean read,
                                                    @RequestParam(defaultValue = "1") long pageNum,
                                                    @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(messageService.pageMessages(type, read, pageNum, pageSize));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        messageService.markRead(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        messageService.deleteOne(id);
        return ApiResponse.success();
    }

    @PostMapping("/batch/read")
    public ApiResponse<Void> batchRead() {
        messageService.batchMarkRead();
        return ApiResponse.success();
    }

    @PostMapping("/batch/clear-read")
    public ApiResponse<Void> clearRead() {
        messageService.batchDeleteRead();
        return ApiResponse.success();
    }
}

