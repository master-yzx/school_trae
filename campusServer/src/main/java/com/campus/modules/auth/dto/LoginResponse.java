package com.campus.modules.auth.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long accessExpiresIn;
    private Long refreshExpiresIn;

    private Long userId;
    private String nickname;
    private String role;
}

