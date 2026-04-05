package com.campus.modules.admin.user.dto;

import lombok.Data;

@Data
public class AdminUserDTO {
    private Long id;
    private String username;
    private String nickname;
    private String studentNo;
    private String phone;
    private String campusName;
    private String registerTime;
    private String status; // NORMAL / DISABLED
}

