package com.campus.modules.favorite.mapper;

import com.campus.modules.favorite.entity.FavoriteCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteCategoryRepository extends JpaRepository<FavoriteCategory, Long> {

    List<FavoriteCategory> findByUserIdOrderByCreatedAtDesc(Long userId);
}

