package com.campus.common.auth;

import java.security.Principal;

public class JwtUserPrincipal implements Principal {

    private final Long userId;
    private final String role;

    public JwtUserPrincipal(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}

