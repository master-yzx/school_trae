package com.campus.modules.admin.content.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.admin.content.dto.AdminProductListDTO;
import com.campus.modules.admin.content.service.AdminContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/content/products")
public class AdminContentController {

    private final AdminContentService adminContentService;

    public AdminContentController(AdminContentService adminContentService) {
        this.adminContentService = adminContentService;
    }

    @GetMapping
    public ApiResponse<PageResult<AdminProductListDTO>> page(@RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String sellerKeyword,
                                                             @RequestParam(required = false) String titleKeyword,
                                                             @RequestParam(required = false) Long categoryId,
                                                             @RequestParam(defaultValue = "1") long pageNum,
                                                             @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(
                adminContentService.pageProducts(status, sellerKeyword, titleKeyword, categoryId, pageNum, pageSize)
        );
    }

    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id, @RequestBody AuditRequest request) {
        adminContentService.audit(id, request.isPass(), request.getReason());
        return ApiResponse.success();
    }

    @PostMapping("/{id}/offline")
    public ApiResponse<Void> offline(@PathVariable Long id) {
        adminContentService.offline(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminContentService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/batch/offline")
    public ApiResponse<Void> batchOffline(@RequestBody IdsRequest ids) {
        adminContentService.batchOffline(ids.getIds());
        return ApiResponse.success();
    }

    @PostMapping("/batch/delete")
    public ApiResponse<Void> batchDelete(@RequestBody IdsRequest ids) {
        adminContentService.batchDelete(ids.getIds());
        return ApiResponse.success();
    }

    @GetMapping("/{id}/reject-reason")
    public ApiResponse<String> rejectReason(@PathVariable Long id) {
        return ApiResponse.success(adminContentService.getRejectReason(id));
    }

    public static class AuditRequest {
        private boolean pass;
        private String reason;

        public boolean isPass() {
            return pass;
        }

        public void setPass(boolean pass) {
            this.pass = pass;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class IdsRequest {
        private Long[] ids;

        public Long[] getIds() {
            return ids;
        }

        public void setIds(Long[] ids) {
            this.ids = ids;
        }
    }
}

