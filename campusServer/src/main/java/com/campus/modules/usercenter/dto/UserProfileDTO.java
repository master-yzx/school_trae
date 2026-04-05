package com.campus.modules.usercenter.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String avatarUrl;
    private String nickname;
    private String role;
    private String studentNo;
    private String phoneMasked;
    private String phone;
    private String campusName;
    private String shopIntro;
}

