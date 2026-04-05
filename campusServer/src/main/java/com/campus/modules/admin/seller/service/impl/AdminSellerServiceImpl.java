package com.campus.modules.admin.seller.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.admin.content.dto.AdminProductListDTO;
import com.campus.modules.admin.seller.dto.AdminSellerDTO;
import com.campus.modules.admin.seller.service.AdminSellerService;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
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
public class AdminSellerServiceImpl implements AdminSellerService {

    private static final String DEFAULT_PASSWORD = "{bcrypt}$2a$10$examplehash";

    private final UserAccountRepository userAccountRepository;
    private final CampusRepository campusRepository;
    private final ProductRepository productRepository;

    public AdminSellerServiceImpl(UserAccountRepository userAccountRepository,
                                 CampusRepository campusRepository,
                                 ProductRepository productRepository) {
        this.userAccountRepository = userAccountRepository;
        this.campusRepository = campusRepository;
        this.productRepository = productRepository;
    }

    @Override
    public PageResult<AdminSellerDTO> pageSellers(String status, String keyword, String campusName,
                                                  String startTime, String endTime, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Specification<UserAccount> spec = (root, q, cb) -> cb.conjunction();

        // 只展示卖家角色账号
        spec = spec.and((root, q, cb) -> cb.equal(root.get("role"), "SELLER"));
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(root.get("username"), like),
                    cb.like(root.get("nickname"), like),
                    cb.like(root.get("studentNo"), like),
                    cb.like(root.get("phone"), like)
            ));
        }

        Long campusId = resolveCampusId(campusName);
        if (campusId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("campusId"), campusId));
        }

        LocalDateTime start = parseStart(startTime);
        if (start != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("registerTime"), start));
        }
        LocalDateTime end = parseEnd(endTime);
        if (end != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("registerTime"), end));
        }

        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps, Sort.by("registerTime").descending());
        Page<UserAccount> page = userAccountRepository.findAll(spec, pageable);

        return PageResults.from(page, pn, ps, this::toDto);
    }

    @Override
    public AdminSellerDTO getDetail(Long id) {
        return userAccountRepository.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public void save(AdminSellerDTO dto) {
        if (dto == null) return;
        UserAccount user = dto.getId() == null
                ? new UserAccount()
                : userAccountRepository.findById(dto.getId()).orElse(new UserAccount());

        if (user.getId() == null) {
            user.setPassword(DEFAULT_PASSWORD);
            user.setRegisterTime(LocalDateTime.now());
            if (user.getUsername() == null || user.getUsername().isBlank()) {
                user.setUsername("seller" + System.currentTimeMillis());
            }
        }

        // 管理端卖家管理创建/编辑的都是卖家角色账号
        user.setRole("SELLER");
        user.setNickname(dto.getNickname());
        user.setStudentNo(dto.getStudentNo());
        user.setPhone(dto.getPhone());
        user.setCampusId(resolveCampusId(dto.getCampusName()));
        user.setStatus(dto.getStatus() == null || dto.getStatus().isBlank() ? "NORMAL" : dto.getStatus());

        userAccountRepository.save(user);
    }

    @Override
    public void disable(Long id) {
        setStatus(id, "DISABLED");
    }

    @Override
    public void enable(Long id) {
        setStatus(id, "NORMAL");
    }

    @Override
    public void delete(Long id) {
        userAccountRepository.deleteById(id);
    }

    @Override
    public void batchDisable(Long[] ids) {
        if (ids == null) return;
        for (Long id : ids) {
            disable(id);
        }
    }

    @Override
    public void batchEnable(Long[] ids) {
        if (ids == null) return;
        for (Long id : ids) {
            enable(id);
        }
    }

    @Override
    public List<AdminProductListDTO> listProducts(Long sellerId) {
        List<Product> products = productRepository.findAll((root, q, cb) -> cb.equal(root.get("sellerId"), sellerId),
                Sort.by("createdAt").descending());

        Map<Long, String> campusNameMap = loadCampusNameMap(products);
        String sellerName = resolveSellerName(sellerId);

        return products.stream().map(p -> {
            AdminProductListDTO dto = new AdminProductListDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setCoverUrl(p.getCoverUrl());
            dto.setPrice(p.getPrice());
            dto.setSellerName(sellerName);
            dto.setCampusName(p.getCampusId() == null ? null : campusNameMap.getOrDefault(p.getCampusId(), ""));
            dto.setStatus(p.getStatus());
            dto.setStatusText(statusText(p.getStatus()));
            dto.setCreatedAt(DateTimes.format(p.getCreatedAt()));
            dto.setRejectReason(p.getRejectReason());
            return dto;
        }).collect(Collectors.toList());
    }

    private AdminSellerDTO toDto(UserAccount user) {
        AdminSellerDTO dto = new AdminSellerDTO();
        dto.setId(user.getId());
        dto.setNickname(user.getNickname() != null && !user.getNickname().isBlank() ? user.getNickname() : user.getUsername());
        dto.setStudentNo(user.getStudentNo());
        dto.setPhone(user.getPhone());
        dto.setCampusName(user.getCampusId() == null ? "" : campusRepository.findById(user.getCampusId()).map(Campus::getName).orElse(""));
        dto.setRegisterTime(DateTimes.format(user.getRegisterTime()));
        dto.setProductCount((int) productRepository.countBySellerId(user.getId()));
        dto.setStatus(user.getStatus());
        return dto;
    }

    private void setStatus(Long id, String status) {
        UserAccount user = userAccountRepository.findById(id).orElse(null);
        if (user == null) return;
        user.setStatus(status);
        userAccountRepository.save(user);
    }

    private String resolveSellerName(Long sellerId) {
        return userAccountRepository.findById(sellerId)
                .map(u -> u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername())
                .orElse("");
    }

    private Long resolveCampusId(String campusName) {
        if (campusName == null || campusName.isBlank()) return null;
        return campusRepository.findFirstByName(campusName.trim()).map(Campus::getId).orElse(null);
    }

    private LocalDateTime parseStart(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date.trim()).atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseEnd(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date.trim()).atTime(23, 59, 59);
        } catch (Exception e) {
            return null;
        }
    }

    private String statusText(String status) {
        if (status == null) return "";
        return switch (status) {
            case "DRAFT" -> "草稿";
            case "PENDING" -> "待审核";
            case "PUBLISHED" -> "已发布";
            case "REJECTED" -> "已驳回";
            case "OFFLINE" -> "已下架";
            default -> status;
        };
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
}

