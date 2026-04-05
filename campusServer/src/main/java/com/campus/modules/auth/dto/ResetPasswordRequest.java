package com.campus.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank
    private String phone;

    /** 短信验证码，未接入短信服务时可留空 */
    private String code;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}

