package com.campus.modules.favorite.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.PageResults;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.favorite.dto.FavoriteCategoryDTO;
import com.campus.modules.favorite.dto.FavoriteItemDTO;
import com.campus.modules.favorite.entity.FavoriteCategory;
import com.campus.modules.favorite.entity.FavoriteItem;
import com.campus.modules.favorite.mapper.FavoriteCategoryRepository;
import com.campus.modules.favorite.mapper.FavoriteItemRepository;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.favorite.service.FavoriteService;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteCategoryRepository favoriteCategoryRepository;
    private final FavoriteItemRepository favoriteItemRepository;
    private final ProductRepository productRepository;
    private final CampusRepository campusRepository;
    private final UserAccountRepository userAccountRepository;

    public FavoriteServiceImpl(FavoriteCategoryRepository favoriteCategoryRepository,
                               FavoriteItemRepository favoriteItemRepository,
                               ProductRepository productRepository,
                               CampusRepository campusRepository,
                               UserAccountRepository userAccountRepository) {
        this.favoriteCategoryRepository = favoriteCategoryRepository;
        this.favoriteItemRepository = favoriteItemRepository;
        this.productRepository = productRepository;
        this.campusRepository = campusRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public List<FavoriteCategoryDTO> listCategories() {
        List<FavoriteCategoryDTO> list = new ArrayList<>();
        FavoriteCategoryDTO all = new FavoriteCategoryDTO();
        all.setId(0L);
        all.setName("全部收藏");
        all.setBuiltIn(true);
        list.add(all);

        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return list;

        List<FavoriteCategory> categories = favoriteCategoryRepository.findByUserIdOrderByCreatedAtDesc(currentUserId);
        for (FavoriteCategory c : categories) {
            FavoriteCategoryDTO dto = new FavoriteCategoryDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setBuiltIn(false);
            list.add(dto);
        }
        return list;
    }

    @Override
    public void addCategory(String name) {
        if (name == null || name.isBlank()) return;
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return;
        FavoriteCategory c = new FavoriteCategory();
        c.setUserId(currentUserId);
        c.setName(name.trim());
        c.setCreatedAt(LocalDateTime.now());
        favoriteCategoryRepository.save(c);
    }

    @Override
    public void addByProductId(Long productId) {
        if (productId == null) return;
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        if (favoriteItemRepository.existsByUserIdAndProductId(currentUserId, productId)) return;
        FavoriteItem item = new FavoriteItem();
        item.setUserId(currentUserId);
        item.setProductId(productId);
        item.setCategoryId(0L);
        item.setCreatedAt(LocalDateTime.now());
        favoriteItemRepository.save(item);
    }

    @Override
    public void removeByProductId(Long productId) {
        if (productId == null) return;
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        favoriteItemRepository.findByUserIdAndProductId(currentUserId, productId)
                .ifPresent(favoriteItemRepository::delete);
    }

    @Override
    public boolean isFavorite(Long productId) {
        if (productId == null) return false;
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return false;
        return favoriteItemRepository.existsByUserIdAndProductId(currentUserId, productId);
    }

    @Override
    public void deleteCategory(Long id) {
        if (id == null) return;
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return;
        favoriteItemRepository.deleteByUserIdAndCategoryId(currentUserId, id);
        favoriteCategoryRepository.deleteById(id);
    }

    @Override
    public PageResult<FavoriteItemDTO> pageFavorites(Long categoryId, String keyword, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            return PageResults.empty(pageNum, pageSize);
        }

        Specification<FavoriteItem> spec = (root, q, cb) -> cb.equal(root.get("userId"), currentUserId);

        if (categoryId != null && categoryId > 0) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("categoryId"), categoryId));
        }

        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            Set<Long> productIds = productRepository.findAll((r, qq, cb) -> cb.like(r.get("title"), like))
                    .stream().map(Product::getId).collect(Collectors.toSet());
            if (productIds.isEmpty()) {
                spec = spec.and((root, q, cb) -> cb.disjunction());
            } else {
                spec = spec.and((root, q, cb) -> root.get("productId").in(productIds));
            }
        }

        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps, Sort.by("createdAt").descending());
        Page<FavoriteItem> page = favoriteItemRepository.findAll(spec, pageable);

        Map<Long, Product> productMap = productRepository.findAllById(
                page.getContent().stream().map(FavoriteItem::getProductId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));

        Map<Long, String> campusNameMap = loadCampusNameMap(productMap.values().stream().toList());
        Map<Long, String> sellerNameMap = loadSellerNameMap(productMap.values().stream().toList());

        return PageResults.from(page, pn, ps, fi -> toDto(fi, productMap, campusNameMap, sellerNameMap));
    }

    @Override
    @Transactional
    public void clearFavorites() {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        favoriteItemRepository.deleteByUserId(currentUserId);
    }

    @Override
    public void removeOne(Long favoriteId) {
        favoriteItemRepository.deleteById(favoriteId);
    }

    private FavoriteItemDTO toDto(FavoriteItem favoriteItem,
                                  Map<Long, Product> productMap,
                                  Map<Long, String> campusNameMap,
                                  Map<Long, String> sellerNameMap) {
        Product p = productMap.get(favoriteItem.getProductId());

        FavoriteItemDTO dto = new FavoriteItemDTO();
        dto.setId(favoriteItem.getId());
        dto.setProductId(favoriteItem.getProductId());

        if (p != null) {
            dto.setTitle(p.getTitle());
            dto.setCoverUrl(p.getCoverUrl());
            dto.setCampusName(p.getCampusId() == null ? null : campusNameMap.getOrDefault(p.getCampusId(), ""));
            dto.setSellerName(sellerNameMap.getOrDefault(p.getSellerId(), ""));
            dto.setPrice(p.getPrice());
        }
        return dto;
    }

    private Map<Long, String> loadCampusNameMap(List<Product> products) {
        Set<Long> campusIds = products.stream()
                .map(Product::getCampusId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        if (campusIds.isEmpty()) return Map.of();

        return campusRepository.findAllById(campusIds).stream()
                .collect(Collectors.toMap(Campus::getId, Campus::getName, (a, b) -> a));
    }

    private Map<Long, String> loadSellerNameMap(List<Product> products) {
        Set<Long> sellerIds = products.stream()
                .map(Product::getSellerId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        if (sellerIds.isEmpty()) return Map.of();

        return userAccountRepository.findAllById(sellerIds).stream()
                .collect(Collectors.toMap(UserAccount::getId,
                        u -> u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername(),
                        (a, b) -> a));
    }
}

