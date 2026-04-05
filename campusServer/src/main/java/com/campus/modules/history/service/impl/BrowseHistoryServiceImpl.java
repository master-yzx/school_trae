package com.campus.modules.history.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.history.dto.BrowseRecordDTO;
import com.campus.modules.history.entity.BrowseHistory;
import com.campus.modules.history.mapper.BrowseHistoryRepository;
import com.campus.modules.history.service.BrowseHistoryService;
import com.campus.modules.favorite.entity.FavoriteItem;
import com.campus.modules.favorite.mapper.FavoriteItemRepository;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class BrowseHistoryServiceImpl implements BrowseHistoryService {

    private final BrowseHistoryRepository browseHistoryRepository;
    private final ProductRepository productRepository;
    private final CampusRepository campusRepository;
    private final UserAccountRepository userAccountRepository;
    private final FavoriteItemRepository favoriteItemRepository;

    public BrowseHistoryServiceImpl(BrowseHistoryRepository browseHistoryRepository,
                                  ProductRepository productRepository,
                                  CampusRepository campusRepository,
                                  UserAccountRepository userAccountRepository,
                                  FavoriteItemRepository favoriteItemRepository) {
        this.browseHistoryRepository = browseHistoryRepository;
        this.productRepository = productRepository;
        this.campusRepository = campusRepository;
        this.userAccountRepository = userAccountRepository;
        this.favoriteItemRepository = favoriteItemRepository;
    }

    @Override
    public PageResult<BrowseRecordDTO> pageHistory(String keyword, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            return PageResults.empty(pageNum, pageSize);
        }

        Specification<BrowseHistory> spec = (root, q, cb) -> cb.equal(root.get("userId"), currentUserId);

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

        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps, Sort.by("viewedAt").descending());
        Page<BrowseHistory> page = browseHistoryRepository.findAll(spec, pageable);

        Map<Long, Product> productMap = productRepository.findAllById(
                page.getContent().stream().map(BrowseHistory::getProductId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));

        Map<Long, String> campusNameMap = loadCampusNameMap(productMap.values().stream().toList());
        Map<Long, String> sellerNameMap = loadSellerNameMap(productMap.values().stream().toList());

        return PageResults.from(page, pn, ps, h -> toDto(h, productMap, campusNameMap, sellerNameMap));
    }

    @Override
    @Transactional
    public void clearAll() {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return;
        browseHistoryRepository.deleteByUserId(currentUserId);
    }

    @Override
    public void deleteOne(Long id) {
        browseHistoryRepository.deleteById(id);
    }

    @Override
    public void addToFavorite(Long id) {
        BrowseHistory history = browseHistoryRepository.findById(id).orElse(null);
        if (history == null) return;
        FavoriteItem item = new FavoriteItem();
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return;
        item.setUserId(currentUserId);
        item.setCategoryId(null);
        item.setProductId(history.getProductId());
        item.setCreatedAt(LocalDateTime.now());
        favoriteItemRepository.save(item);
    }

    @Override
    public void addRecord(Long productId) {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null || productId == null) return;
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;

        // 简单策略：同一用户 + 商品只保留一条，更新时间
        BrowseHistory history = browseHistoryRepository.findOne((root, q, cb) -> cb.and(
                cb.equal(root.get("userId"), currentUserId),
                cb.equal(root.get("productId"), productId)
        )).orElse(null);

        if (history == null) {
            history = new BrowseHistory();
            history.setUserId(currentUserId);
            history.setProductId(productId);
        }
        history.setViewedAt(LocalDateTime.now());
        browseHistoryRepository.save(history);
    }

    private BrowseRecordDTO toDto(BrowseHistory history,
                                  Map<Long, Product> productMap,
                                  Map<Long, String> campusNameMap,
                                  Map<Long, String> sellerNameMap) {
        Product p = productMap.get(history.getProductId());

        BrowseRecordDTO dto = new BrowseRecordDTO();
        dto.setId(history.getId());
        dto.setProductId(history.getProductId());
        dto.setViewedAt(DateTimes.format(history.getViewedAt()));

        if (p != null) {
            dto.setTitle(p.getTitle());
            dto.setCoverUrl(p.getCoverUrl());
            dto.setCampusName(p.getCampusId() == null ? null : campusNameMap.getOrDefault(p.getCampusId(), ""));
            dto.setSellerName(sellerNameMap.getOrDefault(p.getSellerId(), ""));
            dto.setPrice(p.getPrice());
            dto.setConditionText(p.getConditionText());
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

