package com.campus.modules.admin.manager.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.admin.manager.dto.AdminAccountDTO;
import com.campus.modules.admin.manager.service.AdminManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/managers")
@RequiredArgsConstructor
public class AdminManagerController {

    private final AdminManagerService adminManagerService;

    @GetMapping
    public ApiResponse<PageResult<AdminAccountDTO>> pageList(@RequestParam(defaultValue = "1") long pageNum,
                                                             @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(adminManagerService.pageList(pageNum, pageSize));
    }

    /**
     * 检查系统中是否已存在指定用户名的用户（用来在前端弹框确认是否升级为管理员）。
     */
    @GetMapping("/check-user")
    public ApiResponse<Boolean> checkUser(@RequestParam String username) {
        return ApiResponse.success(adminManagerService.existsUser(username));
    }

    /**
     * 获取当前登录管理员的权限列表。
     */
    @GetMapping("/me/permissions")
    public ApiResponse<java.util.List<String>> myPermissions() {
        return ApiResponse.success(adminManagerService.getCurrentPermissions());
    }

    @PostMapping
    public ApiResponse<Void> save(@RequestBody AdminAccountDTO dto) {
        adminManagerService.save(dto);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminManagerService.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/permissions")
    public ApiResponse<Void> assignPermissions(@PathVariable Long id, @RequestBody List<String> permissions) {
        adminManagerService.assignPermissions(id, permissions);
        return ApiResponse.success(null);
    }
}

