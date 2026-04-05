package com.campus.modules.product.mapper;

import com.campus.modules.product.entity.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductMediaRepository extends JpaRepository<ProductMedia, Long> {

    List<ProductMedia> findByProductIdOrderBySortOrderAsc(Long productId);
}

