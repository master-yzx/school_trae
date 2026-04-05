package com.campus.modules.auth.dto;

import lombok.Data;

@Data
public class LoginCaptchaResponse {

    private String captchaId;

    /**
     * 验证码图片数据（data URL，例如 data:image/svg+xml;base64,...）
     */
    private String imageData;

    /**
     * 可选：验证码原始文本（前端不必使用）
     */
    private String code;
}

