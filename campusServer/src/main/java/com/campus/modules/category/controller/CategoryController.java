package com.campus.modules.category.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.category.dto.CategoryTreeNode;
import com.campus.modules.category.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryTreeNode>> tree() {
        return ApiResponse.success(categoryService.listCategoryTree());
    }
}

