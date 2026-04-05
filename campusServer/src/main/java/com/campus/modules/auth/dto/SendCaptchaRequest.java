package com.campus.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendCaptchaRequest {

    @NotBlank
    private String phone;

    /**
     * 类型：LOGIN / REGISTER / RESET
     */
    @NotBlank
    private String type;
}

