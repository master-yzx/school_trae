package com.campus.modules.admin.review.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.admin.review.dto.AdminReviewProductDTO;
import com.campus.modules.admin.review.service.AdminReviewService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/review/products")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    public AdminReviewController(AdminReviewService adminReviewService) {
        this.adminReviewService = adminReviewService;
    }

    @GetMapping
    public ApiResponse<PageResult<AdminReviewProductDTO>> page(@RequestParam(required = false) String status,
                                                               @RequestParam(required = false) String sellerKeyword,
                                                               @RequestParam(required = false) String titleKeyword,
                                                               @RequestParam(defaultValue = "1") long pageNum,
                                                               @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(adminReviewService.pageReviewProducts(status, sellerKeyword, titleKeyword, pageNum, pageSize));
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        adminReviewService.approve(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id, @RequestBody RejectRequest request) {
        adminReviewService.reject(id, request.getReason());
        return ApiResponse.success();
    }

    public static class RejectRequest {
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}

