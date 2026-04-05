package com.campus.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    /**
     * 可以是手机号或学号
     */
    @NotBlank
    private String account;

    @NotBlank
    private String password;

    /**
     * 图形验证码 ID
     */
    private String captchaId;

    private String captcha;
}

