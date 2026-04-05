package com.campus.modules.admin.category.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.admin.category.dto.AdminCategoryNodeDTO;
import com.campus.modules.admin.category.service.AdminCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    public AdminCategoryController(AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<AdminCategoryNodeDTO>> tree() {
        return ApiResponse.success(adminCategoryService.listTree());
    }

    @PostMapping("/save")
    public ApiResponse<Void> save(@RequestBody AdminCategoryNodeDTO dto) {
        adminCategoryService.save(dto);
        return ApiResponse.success();
    }

    @PostMapping("/sort")
    public ApiResponse<Void> sort(@RequestBody List<AdminCategoryNodeDTO> tree) {
        adminCategoryService.sort(tree);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/toggle")
    public ApiResponse<Void> toggle(@PathVariable Long id) {
        adminCategoryService.toggle(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminCategoryService.delete(id);
        return ApiResponse.success();
    }
}

