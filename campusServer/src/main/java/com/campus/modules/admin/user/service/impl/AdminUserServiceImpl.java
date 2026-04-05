package com.campus.modules.admin.user.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.admin.user.dto.AdminUserDTO;
import com.campus.modules.admin.user.service.AdminUserService;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final String DEFAULT_PASSWORD = "{bcrypt}$2a$10$examplehash";

    private final UserAccountRepository userAccountRepository;
    private final CampusRepository campusRepository;

    public AdminUserServiceImpl(UserAccountRepository userAccountRepository, CampusRepository campusRepository) {
        this.userAccountRepository = userAccountRepository;
        this.campusRepository = campusRepository;
    }

    @Override
    public PageResult<AdminUserDTO> pageUsers(String status, String keyword, String campusName,
                                              String startTime, String endTime, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Specification<UserAccount> spec = (root, q, cb) -> cb.conjunction();

        // 只展示普通用户，不包含卖家/管理员
        spec = spec.and((root, q, cb) -> cb.equal(root.get("role"), "USER"));

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
    public AdminUserDTO getDetail(Long id) {
        return userAccountRepository.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public void save(AdminUserDTO dto) {
        if (dto == null) return;
        UserAccount user = dto.getId() == null
                ? new UserAccount()
                : userAccountRepository.findById(dto.getId()).orElse(new UserAccount());

        if (user.getId() == null) {
            user.setPassword(DEFAULT_PASSWORD);
            user.setRegisterTime(LocalDateTime.now());
        }

        // 管理端用户管理创建/编辑的都是普通用户角色
        user.setRole("USER");
        user.setUsername(dto.getUsername());
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

    private AdminUserDTO toDto(UserAccount user) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setStudentNo(user.getStudentNo());
        dto.setPhone(user.getPhone());
        dto.setCampusName(user.getCampusId() == null ? "" : campusRepository.findById(user.getCampusId()).map(c -> c.getName()).orElse(""));
        dto.setRegisterTime(DateTimes.format(user.getRegisterTime()));
        dto.setStatus(user.getStatus());
        return dto;
    }

    private void setStatus(Long id, String status) {
        UserAccount user = userAccountRepository.findById(id).orElse(null);
        if (user == null) return;
        user.setStatus(status);
        userAccountRepository.save(user);
    }

    private Long resolveCampusId(String campusName) {
        if (campusName == null || campusName.isBlank()) return null;
        return campusRepository.findFirstByName(campusName.trim()).map(c -> c.getId()).orElse(null);
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
}

