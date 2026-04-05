package com.campus.modules.admin.category.service.impl;

import com.campus.modules.admin.category.dto.AdminCategoryNodeDTO;
import com.campus.modules.admin.category.service.AdminCategoryService;
import com.campus.modules.category.entity.Category;
import com.campus.modules.category.mapper.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    public AdminCategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<AdminCategoryNodeDTO> listTree() {
        List<Category> categories = categoryRepository.findAll();
        // 先按 sortOrder 排序，null 视为 0
        categories.sort(Comparator.comparingInt(c -> c.getSortOrder() == null ? 0 : c.getSortOrder()));

        Map<Long, List<Category>> childrenMap = categories.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));

        return buildTree(childrenMap, 0L);
    }

    private List<AdminCategoryNodeDTO> buildTree(Map<Long, List<Category>> childrenMap, Long parentId) {
        List<Category> children = childrenMap.getOrDefault(parentId, new ArrayList<>());
        return children.stream().map(c -> {
            AdminCategoryNodeDTO node = new AdminCategoryNodeDTO();
            node.setId(c.getId());
            node.setParentId(c.getParentId() == null ? 0L : c.getParentId());
            node.setName(c.getName());
            node.setEnabled(Boolean.TRUE.equals(c.getEnabled()));
            node.setSortOrder(c.getSortOrder());
            node.setChildren(buildTree(childrenMap, c.getId()));
            return node;
        }).collect(Collectors.toList());
    }

    @Override
    public void save(AdminCategoryNodeDTO dto) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            return;
        }
        Category category;
        if (dto.getId() != null) {
            category = categoryRepository.findById(dto.getId()).orElse(new Category());
        } else {
            category = new Category();
            // 新建时为当前父级末尾
            Long parentId = normalizeParentId(dto.getParentId());
            Integer nextSort = nextSortOrder(parentId);
            category.setParentId(parentId);
            category.setSortOrder(nextSort);
        }

        category.setName(dto.getName().trim());
        category.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : Boolean.TRUE);
        // 允许在编辑时调整父级
        if (dto.getId() != null) {
            category.setParentId(normalizeParentId(dto.getParentId()));
        }

        categoryRepository.save(category);
    }

    @Override
    public void sort(List<AdminCategoryNodeDTO> tree) {
        if (tree == null || tree.isEmpty()) return;
        List<Category> all = categoryRepository.findAll();
        Map<Long, Category> map = all.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(Category::getId, c -> c));

        applySort(tree, 0L, map);
        categoryRepository.saveAll(map.values());
    }

    private void applySort(List<AdminCategoryNodeDTO> nodes, Long parentId, Map<Long, Category> map) {
        if (nodes == null) return;
        for (int i = 0; i < nodes.size(); i++) {
            AdminCategoryNodeDTO n = nodes.get(i);
            if (n.getId() == null) continue;
            Category c = map.get(n.getId());
            if (c == null) continue;
            c.setParentId(Objects.equals(parentId, 0L) ? null : parentId);
            c.setSortOrder(i + 1);
            applySort(n.getChildren(), n.getId(), map);
        }
    }

    @Override
    public void toggle(Long id) {
        if (id == null) return;
        Category c = categoryRepository.findById(id).orElse(null);
        if (c == null) return;
        c.setEnabled(!Boolean.TRUE.equals(c.getEnabled()));
        categoryRepository.save(c);
    }

    @Override
    public void delete(Long id) {
        if (id == null) return;
        List<Category> all = categoryRepository.findAll();
        List<Long> toDelete = new ArrayList<>();
        collectSubtreeIds(all, id, toDelete);
        if (toDelete.isEmpty()) return;
        categoryRepository.deleteAll(
                all.stream().filter(c -> toDelete.contains(c.getId())).toList()
        );
    }

    private void collectSubtreeIds(List<Category> all, Long id, List<Long> acc) {
        for (Category c : all) {
            if (Objects.equals(c.getId(), id)) {
                acc.add(c.getId());
            }
        }
        for (Category c : all) {
            if (Objects.equals(c.getParentId(), id)) {
                collectSubtreeIds(all, c.getId(), acc);
            }
        }
    }

    private Long normalizeParentId(Long parentId) {
        if (parentId == null || parentId <= 0) return null;
        return parentId;
    }

    private int nextSortOrder(Long parentId) {
        List<Category> all = categoryRepository.findAll();
        int max = all.stream()
                .filter(c -> Objects.equals(
                        c.getParentId() == null ? 0L : c.getParentId(),
                        parentId == null ? 0L : parentId))
                .map(c -> c.getSortOrder() == null ? 0 : c.getSortOrder())
                .max(Integer::compareTo)
                .orElse(0);
        return max + 1;
    }
}

