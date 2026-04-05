package com.campus.modules.admin.user.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.admin.user.dto.AdminUserDTO;
import com.campus.modules.admin.user.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ApiResponse<PageResult<AdminUserDTO>> page(@RequestParam(required = false) String status,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) String campusName,
                                                      @RequestParam(required = false) String startTime,
                                                      @RequestParam(required = false) String endTime,
                                                      @RequestParam(defaultValue = "1") long pageNum,
                                                      @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(
                adminUserService.pageUsers(status, keyword, campusName, startTime, endTime, pageNum, pageSize)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminUserDTO> detail(@PathVariable Long id) {
        return ApiResponse.success(adminUserService.getDetail(id));
    }

    @PostMapping("/save")
    public ApiResponse<Void> save(@RequestBody AdminUserDTO dto) {
        adminUserService.save(dto);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/disable")
    public ApiResponse<Void> disable(@PathVariable Long id) {
        adminUserService.disable(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/enable")
    public ApiResponse<Void> enable(@PathVariable Long id) {
        adminUserService.enable(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminUserService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/batch/disable")
    public ApiResponse<Void> batchDisable(@RequestBody IdsRequest ids) {
        adminUserService.batchDisable(ids.getIds());
        return ApiResponse.success();
    }

    @PostMapping("/batch/enable")
    public ApiResponse<Void> batchEnable(@RequestBody IdsRequest ids) {
        adminUserService.batchEnable(ids.getIds());
        return ApiResponse.success();
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

