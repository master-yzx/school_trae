package com.campus.modules.usercenter.service;

import com.campus.modules.usercenter.dto.*;

import java.util.List;

public interface UserCenterService {

    UserProfileDTO getProfile();

    List<UserFeatureEntryDTO> listFeatureEntries();

    int getUnreadMessageCount();

    UserUnreadCountsDTO getUnreadCounts();

    void updateProfile(UpdateProfileRequest request);

    void updateAvatar(String avatarUrl);

    void changePassword(ChangePasswordRequest request);
}

