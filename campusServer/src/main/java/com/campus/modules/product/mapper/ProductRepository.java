package com.campus.modules.product.mapper;

import com.campus.modules.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findByStatus(String status, Pageable pageable);

    Page<Product> findByStatusAndTitleContainingIgnoreCase(String status, String keyword, Pageable pageable);

    List<Product> findTop12ByStatusOrderByCreatedAtDesc(String status);

    List<Product> findTop10ByStatusAndCampusIdOrderByCreatedAtDesc(String status, Long campusId);

    List<Product> findTop6ByStatusAndCategoryIdAndIdNotOrderByCreatedAtDesc(String status, Long categoryId, Long id);

    long countBySellerId(Long sellerId);
}

