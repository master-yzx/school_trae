package com.campus.modules.category.mapper;

import com.campus.modules.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByEnabledTrueOrderBySortOrderAsc();
}

