package com.campus.modules.usercenter.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.usercenter.dto.*;
import com.campus.modules.usercenter.service.UserCenterService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserCenterController {

    private final UserCenterService userCenterService;

    public UserCenterController(UserCenterService userCenterService) {
        this.userCenterService = userCenterService;
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileDTO> profile() {
        return ApiResponse.success(userCenterService.getProfile());
    }

    @GetMapping("/features")
    public ApiResponse<List<UserFeatureEntryDTO>> features() {
        return ApiResponse.success(userCenterService.listFeatureEntries());
    }

    @GetMapping("/unread-count")
    public ApiResponse<Integer> unreadCount() {
        return ApiResponse.success(userCenterService.getUnreadMessageCount());
    }

    @GetMapping("/unread-counts")
    public ApiResponse<UserUnreadCountsDTO> unreadCounts() {
        return ApiResponse.success(userCenterService.getUnreadCounts());
    }

    @PostMapping("/profile/update")
    public ApiResponse<Void> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        userCenterService.updateProfile(request);
        return ApiResponse.success();
    }

    @PostMapping("/profile/avatar")
    public ApiResponse<Void> updateAvatar(@Valid @RequestBody UpdateAvatarRequest request) {
        userCenterService.updateAvatar(request.getAvatarUrl());
        return ApiResponse.success();
    }

    @PostMapping("/password/change")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userCenterService.changePassword(request);
        return ApiResponse.success();
    }
}

