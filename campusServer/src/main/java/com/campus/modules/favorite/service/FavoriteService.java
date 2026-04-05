package com.campus.modules.favorite.service;

import com.campus.common.result.PageResult;
import com.campus.modules.favorite.dto.FavoriteCategoryDTO;
import com.campus.modules.favorite.dto.FavoriteItemDTO;

import java.util.List;

public interface FavoriteService {

    List<FavoriteCategoryDTO> listCategories();

    void addCategory(String name);

    void deleteCategory(Long id);

    void addByProductId(Long productId);

    void removeByProductId(Long productId);

    boolean isFavorite(Long productId);

    PageResult<FavoriteItemDTO> pageFavorites(Long categoryId, String keyword, long pageNum, long pageSize);

    void clearFavorites();

    void removeOne(Long favoriteId);
}

