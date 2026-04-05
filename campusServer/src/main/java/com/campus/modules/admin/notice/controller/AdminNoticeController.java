package com.campus.modules.admin.notice.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.notice.dto.NoticeDTO;
import com.campus.modules.notice.dto.NoticeSaveRequest;
import com.campus.modules.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ApiResponse<PageResult<NoticeDTO>> page(@RequestParam(required = false) String type,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Boolean enabled,
                                                   @RequestParam(defaultValue = "1") long pageNum,
                                                   @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(noticeService.pageAdmin(type, keyword, enabled, pageNum, pageSize));
    }

    @PostMapping
    public ApiResponse<NoticeDTO> save(@RequestBody NoticeSaveRequest request) {
        return ApiResponse.success(noticeService.save(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ApiResponse.success(null);
    }
}

