package com.campus.modules.admin.category.service;

import com.campus.modules.admin.category.dto.AdminCategoryNodeDTO;

import java.util.List;

public interface AdminCategoryService {

    List<AdminCategoryNodeDTO> listTree();

    void save(AdminCategoryNodeDTO dto);

    void sort(List<AdminCategoryNodeDTO> tree);

    void toggle(Long id);

    void delete(Long id);
}

