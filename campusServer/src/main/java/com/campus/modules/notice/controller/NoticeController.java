package com.campus.modules.notice.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.notice.dto.NoticeDTO;
import com.campus.modules.notice.service.NoticeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public ApiResponse<PageResult<NoticeDTO>> page(@RequestParam(required = false) String type,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(defaultValue = "1") long pageNum,
                                                   @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(noticeService.pagePublic(type, keyword, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<NoticeDTO> detail(@PathVariable Long id) {
        return ApiResponse.success(noticeService.getPublicDetail(id));
    }
}

