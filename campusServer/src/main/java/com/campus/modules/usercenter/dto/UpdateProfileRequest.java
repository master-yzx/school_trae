package com.campus.modules.usercenter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank
    private String nickname;

    private String phone;

    private String studentNo;

    private String campusName;

    private String shopIntro;
}

