package com.campus.modules.usercenter.service.impl;

import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import com.campus.modules.chat.mapper.ChatSessionRepository;
import com.campus.modules.message.mapper.MessageRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import com.campus.modules.usercenter.dto.*;
import com.campus.modules.usercenter.service.UserCenterService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserCenterServiceImpl implements UserCenterService {

    private final UserAccountRepository userAccountRepository;
    private final CampusRepository campusRepository;
    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserCenterServiceImpl(UserAccountRepository userAccountRepository,
                                CampusRepository campusRepository,
                                MessageRepository messageRepository,
                                ChatSessionRepository chatSessionRepository) {
        this.userAccountRepository = userAccountRepository;
        this.campusRepository = campusRepository;
        this.messageRepository = messageRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    @Override
    public UserProfileDTO getProfile() {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return null;
        UserAccount user = userAccountRepository.findById(currentUserId).orElse(null);
        if (user == null) return null;

        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        String avatar = user.getAvatarUrl();
        if (avatar == null || avatar.isBlank()) {
            avatar = "https://via.placeholder.com/80x80?text=U";
        }
        dto.setAvatarUrl(avatar);
        dto.setNickname(user.getNickname() != null && !user.getNickname().isBlank() ? user.getNickname() : user.getUsername());
        dto.setRole(user.getRole() == null ? "USER" : user.getRole());
        dto.setStudentNo(user.getStudentNo());
        dto.setPhoneMasked(maskPhone(user.getPhone()));
        dto.setPhone(user.getPhone());
        dto.setCampusName(user.getCampusId() == null ? "" : campusRepository.findById(user.getCampusId()).map(c -> c.getName()).orElse(""));
        return dto;
    }

    @Override
    public List<UserFeatureEntryDTO> listFeatureEntries() {
        List<UserFeatureEntryDTO> list = new ArrayList<>();

        list.add(build("seller-products", "我的商品"));
        // 卖家专属：查看别人购买我发布商品的订单
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId != null) {
            UserAccount ua = userAccountRepository.findById(currentUserId).orElse(null);
            if (ua != null && "SELLER".equalsIgnoreCase(ua.getRole())) {
                list.add(build("seller-orders", "卖家订单"));
            }
        }
        list.add(build("favorite", "我的收藏"));
        list.add(build("history", "浏览记录"));
        list.add(build("order", "我的订单"));
        list.add(build("cart", "购物车"));
        list.add(build("settings", "账号设置"));

        return list;
    }

    @Override
    public int getUnreadMessageCount() {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return 0;
        return (int) messageRepository.countByUserIdAndReadFalse(currentUserId);
    }

    @Override
    public UserUnreadCountsDTO getUnreadCounts() {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        UserUnreadCountsDTO dto = new UserUnreadCountsDTO();
        if (currentUserId == null) {
            dto.setMessageUnread(0);
            dto.setChatUnread(0);
            return dto;
        }

        dto.setMessageUnread((int) messageRepository.countByUserIdAndReadFalse(currentUserId));
        long chatUnread = chatSessionRepository.sumUnreadBuyerByBuyerId(currentUserId)
                + chatSessionRepository.sumUnreadSellerBySellerId(currentUserId);
        dto.setChatUnread((int) Math.min(Integer.MAX_VALUE, Math.max(0, chatUnread)));
        return dto;
    }

    @Override
    public void updateProfile(UpdateProfileRequest request) {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return;

        UserAccount user = userAccountRepository.findById(currentUserId).orElse(null);
        if (user == null) {
            return;
        }

        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname().trim());
        }

        if (request.getPhone() != null) {
            String p = request.getPhone().trim();
            user.setPhone(p.isEmpty() ? null : p);
        }

        if (request.getStudentNo() != null) {
            String sn = request.getStudentNo().trim();
            user.setStudentNo(sn.isEmpty() ? null : sn);
        }

        if (request.getCampusName() != null) {
            String campusName = request.getCampusName().trim();
            if (campusName.isEmpty()) {
                user.setCampusId(null);
            } else {
                Campus campus = campusRepository.findFirstByName(campusName).orElse(null);
                if (campus != null) {
                    user.setCampusId(campus.getId());
                }
            }
        }

        userAccountRepository.save(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return;
        }
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return;

        UserAccount user = userAccountRepository.findById(currentUserId).orElse(null);
        if (user == null) return;

        user.setAvatarUrl(avatarUrl.trim());
        userAccountRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return;
        }
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return;

        UserAccount user = userAccountRepository.findById(currentUserId).orElse(null);
        if (user == null) return;

        String stored = user.getPassword();
        if (!matchesPassword(request.getOldPassword(), stored)) {
            return;
        }

        String encoded = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encoded);
        userAccountRepository.save(user);
    }

    private UserFeatureEntryDTO build(String code, String name) {
        UserFeatureEntryDTO dto = new UserFeatureEntryDTO();
        dto.setCode(code);
        dto.setName(name);
        dto.setIcon(code);
        return dto;
    }

    private String maskPhone(String phone) {
        if (phone == null) return null;
        String p = phone.trim();
        if (p.length() < 7) return p;
        return p.substring(0, 3) + "****" + p.substring(p.length() - 4);
    }

    private boolean matchesPassword(String raw, String stored) {
        if (raw == null || stored == null) return false;
        String s = stored.trim();
        if (s.startsWith("{bcrypt}")) {
            s = s.substring("{bcrypt}".length());
        }
        if (s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$")) {
            return passwordEncoder.matches(raw, s);
        }
        return raw.equals(stored);
    }
}

