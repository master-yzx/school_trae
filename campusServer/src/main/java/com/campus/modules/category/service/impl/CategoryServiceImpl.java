package com.campus.modules.category.service.impl;

import com.campus.modules.category.dto.CategoryTreeNode;
import com.campus.modules.category.entity.Category;
import com.campus.modules.category.mapper.CategoryRepository;
import com.campus.modules.category.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryTreeNode> listCategoryTree() {
        List<Category> categories = categoryRepository.findByEnabledTrueOrderBySortOrderAsc();
        Map<Long, List<Category>> childrenMap = categories.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));
        return buildTree(childrenMap, 0L);
    }

    private List<CategoryTreeNode> buildTree(Map<Long, List<Category>> childrenMap, Long parentId) {
        List<Category> children = childrenMap.getOrDefault(parentId, new ArrayList<>());
        return children.stream().map(c -> {
            CategoryTreeNode node = new CategoryTreeNode();
            node.setId(c.getId());
            node.setName(c.getName());
            node.setChildren(buildTree(childrenMap, c.getId()));
            return node;
        }).collect(Collectors.toList());
    }
}

