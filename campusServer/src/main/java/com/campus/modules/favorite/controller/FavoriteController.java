package com.campus.modules.favorite.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.favorite.dto.FavoriteCategoryDTO;
import com.campus.modules.favorite.dto.FavoriteItemDTO;
import com.campus.modules.favorite.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<FavoriteCategoryDTO>> categories() {
        return ApiResponse.success(favoriteService.listCategories());
    }

    @PostMapping("/categories")
    public ApiResponse<Void> addCategory(@RequestParam String name) {
        favoriteService.addCategory(name);
        return ApiResponse.success();
    }

    @DeleteMapping("/categories/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        favoriteService.deleteCategory(id);
        return ApiResponse.success();
    }

    @PostMapping("/add")
    public ApiResponse<Void> addByProduct(@RequestParam Long productId) {
        favoriteService.addByProductId(productId);
        return ApiResponse.success();
    }

    @DeleteMapping("/product/{productId}")
    public ApiResponse<Void> removeByProduct(@PathVariable Long productId) {
        favoriteService.removeByProductId(productId);
        return ApiResponse.success();
    }

    @GetMapping("/check")
    public ApiResponse<Boolean> check(@RequestParam Long productId) {
        return ApiResponse.success(favoriteService.isFavorite(productId));
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<FavoriteItemDTO>> list(@RequestParam(required = false) Long categoryId,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam(defaultValue = "1") long pageNum,
                                                         @RequestParam(defaultValue = "12") long pageSize) {
        return ApiResponse.success(favoriteService.pageFavorites(categoryId, keyword, pageNum, pageSize));
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clear() {
        favoriteService.clearFavorites();
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> remove(@PathVariable Long id) {
        favoriteService.removeOne(id);
        return ApiResponse.success();
    }
}

