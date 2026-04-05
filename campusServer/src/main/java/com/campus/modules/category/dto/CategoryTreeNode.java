package com.campus.modules.category.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeNode {
    private Long id;
    private String name;
    private List<CategoryTreeNode> children;
}

