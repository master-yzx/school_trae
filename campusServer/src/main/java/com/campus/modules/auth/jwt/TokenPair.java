package com.campus.modules.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenPair {

    private String accessToken;
    private String refreshToken;

    private long accessExpiresIn;
    private long refreshExpiresIn;

    private String accessJti;
    private String refreshJti;
}

