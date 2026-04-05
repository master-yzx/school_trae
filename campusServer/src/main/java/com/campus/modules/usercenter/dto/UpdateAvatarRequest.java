package com.campus.modules.usercenter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAvatarRequest {

    @NotBlank
    private String avatarUrl;
}

