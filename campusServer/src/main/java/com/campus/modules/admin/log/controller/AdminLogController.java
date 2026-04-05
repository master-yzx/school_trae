package com.campus.modules.admin.log.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.admin.log.dto.AdminLogDTO;
import com.campus.modules.admin.log.service.AdminLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {

    private final AdminLogService adminLogService;

    @GetMapping
    public ApiResponse<PageResult<AdminLogDTO>> pageList(@RequestParam(required = false) String operator,
                                                         @RequestParam(required = false) String type,
                                                         @RequestParam(required = false) String result,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) String startTime,
                                                         @RequestParam(required = false) String endTime,
                                                         @RequestParam(defaultValue = "1") long pageNum,
                                                         @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(
                adminLogService.pageList(operator, type, result, keyword, startTime, endTime, pageNum, pageSize)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminLogDTO> detail(@PathVariable Long id) {
        return ApiResponse.success(adminLogService.detail(id));
    }

    @PostMapping("/export")
    public ApiResponse<String> export(@RequestParam(required = false) String operator,
                                      @RequestParam(required = false) String type,
                                      @RequestParam(required = false) String result,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String startTime,
                                      @RequestParam(required = false) String endTime) {
        String url = adminLogService.export(operator, type, result, keyword, startTime, endTime);
        return ApiResponse.success(url);
    }
}

