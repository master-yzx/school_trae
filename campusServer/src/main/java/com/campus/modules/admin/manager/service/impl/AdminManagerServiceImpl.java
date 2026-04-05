package com.campus.modules.admin.manager.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.admin.manager.dto.AdminAccountDTO;
import com.campus.modules.admin.manager.entity.AdminAccount;
import com.campus.modules.admin.manager.mapper.AdminAccountRepository;
import com.campus.modules.admin.manager.service.AdminManagerService;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminManagerServiceImpl implements AdminManagerService {

    private final AdminAccountRepository adminAccountRepository;
    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminManagerServiceImpl(AdminAccountRepository adminAccountRepository,
                                   UserAccountRepository userAccountRepository) {
        this.adminAccountRepository = adminAccountRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public PageResult<AdminAccountDTO> pageList(long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);
        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps);
        Page<AdminAccount> page = adminAccountRepository.findByOrderByCreatedAtDesc(pageable);
        return PageResults.from(page, pn, ps, this::toDto);
    }

    @Override
    public boolean existsUser(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        Optional<UserAccount> optional = userAccountRepository
                .findFirstByUsernameOrPhoneOrStudentNo(username.trim(), username.trim(), username.trim());
        return optional.isPresent();
    }

    @Override
    public List<String> getCurrentPermissions() {
        var current = com.campus.common.auth.CurrentUserHolder.get();
        if (current == null) {
            return List.of();
        }
        Long userId = current.getId();
        if (userId == null) {
            return List.of();
        }
        UserAccount ua = userAccountRepository.findById(userId).orElse(null);
        if (ua == null || ua.getUsername() == null) {
            return List.of();
        }
        return adminAccountRepository.findFirstByUsername(ua.getUsername())
                .map(a -> {
                    if (a.getPermissions() == null || a.getPermissions().isBlank()) {
                        return List.<String>of();
                    }
                    return Arrays.stream(a.getPermissions().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                })
                .orElse(List.of());
    }

    @Override
    public void save(AdminAccountDTO dto) {
        if (dto == null || dto.getUsername() == null || dto.getUsername().isBlank()) {
            return;
        }
        AdminAccount entity = dto.getId() == null
                ? new AdminAccount()
                : adminAccountRepository.findById(dto.getId()).orElse(new AdminAccount());

        String username = dto.getUsername().trim();
        entity.setUsername(username);
        entity.setRoleName(dto.getRoleName() == null || dto.getRoleName().isBlank()
                ? "普通管理员"
                : dto.getRoleName().trim());
        entity.setPhone(dto.getPhone());
        if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
            entity.setPermissions(String.join(",", dto.getPermissions()));
        }

        if (entity.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }

        // 同步 / 创建对应的前台账号，并赋予 ADMIN 角色
        UserAccount user = userAccountRepository
                .findFirstByUsernameOrPhoneOrStudentNo(username, username, username)
                .orElse(null);
        if (user == null) {
            user = new UserAccount();
            user.setUsername(username);
            user.setPhone(dto.getPhone());
            String rawPassword = (dto.getPassword() == null || dto.getPassword().isBlank())
                    ? "admin123"
                    : dto.getPassword();
            user.setPassword(passwordEncoder.encode(rawPassword));
            String nickSource = dto.getPhone() != null && !dto.getPhone().isBlank()
                    ? dto.getPhone()
                    : username;
            String suffix = nickSource.length() > 4
                    ? nickSource.substring(nickSource.length() - 4)
                    : nickSource;
            user.setNickname("管理员" + suffix);
            user.setStudentNo(null);
            user.setCampusId(null);
            user.setAvatarUrl(null);
            user.setRole("ADMIN");
            user.setStatus("NORMAL");
            user.setRegisterTime(LocalDateTime.now());
        } else {
            if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
                user.setPhone(dto.getPhone());
            }
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            user.setRole("ADMIN");
            if (user.getStatus() == null || user.getStatus().isBlank()) {
                user.setStatus("NORMAL");
            }
            if (user.getRegisterTime() == null) {
                user.setRegisterTime(LocalDateTime.now());
            }
        }
        userAccountRepository.save(user);

        // 为了兼容旧字段，这里始终存一个占位密码，不参与登录校验
        if (entity.getPassword() == null || entity.getPassword().isBlank()) {
            entity.setPassword("admin123");
        }

        adminAccountRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
        if (id == null) return;
        adminAccountRepository.deleteById(id);
    }

    @Override
    public void assignPermissions(Long id, List<String> permissions) {
        if (id == null) return;
        AdminAccount entity = adminAccountRepository.findById(id).orElse(null);
        if (entity == null) return;
        if (permissions == null || permissions.isEmpty()) {
            entity.setPermissions(null);
        } else {
            entity.setPermissions(String.join(",", permissions));
        }
        adminAccountRepository.save(entity);
    }

    private AdminAccountDTO toDto(AdminAccount entity) {
        AdminAccountDTO dto = new AdminAccountDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setRoleName(entity.getRoleName());
        dto.setPhone(entity.getPhone());
        dto.setCreatedAt(entity.getCreatedAt() == null ? null : DateTimes.format(entity.getCreatedAt()));
        if (entity.getPermissions() != null && !entity.getPermissions().isBlank()) {
            dto.setPermissions(Arrays.stream(entity.getPermissions().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()));
        } else {
            dto.setPermissions(List.of());
        }
        return dto;
    }
}

