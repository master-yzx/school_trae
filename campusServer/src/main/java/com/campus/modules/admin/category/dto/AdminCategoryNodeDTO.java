package com.campus.modules.admin.category.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminCategoryNodeDTO {
    private Long id;
    private Long parentId;
    private String name;
    private Boolean enabled;
    private Integer sortOrder;
    private List<AdminCategoryNodeDTO> children;
}

