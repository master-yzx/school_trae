package com.campus.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String phone;

    /**
     * 用户名，作为登录账号。
     */
    @NotBlank
    private String username;

    /** 短信验证码，未接入短信服务时可留空 */
    private String code;

    private String studentNo;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    /**
     * 角色：USER / SELLER
     */
    @NotBlank
    private String role;
}

