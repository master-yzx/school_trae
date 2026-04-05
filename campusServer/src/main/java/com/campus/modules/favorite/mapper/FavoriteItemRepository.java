package com.campus.modules.favorite.mapper;

import com.campus.modules.favorite.entity.FavoriteItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long>, JpaSpecificationExecutor<FavoriteItem> {

    Page<FavoriteItem> findByUserId(Long userId, Pageable pageable);

    Page<FavoriteItem> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    void deleteByUserId(Long userId);

    void deleteByUserIdAndCategoryId(Long userId, Long categoryId);

    Optional<FavoriteItem> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}

