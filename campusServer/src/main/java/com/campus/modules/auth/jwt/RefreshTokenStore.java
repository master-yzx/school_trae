package com.campus.modules.auth.jwt;

public interface RefreshTokenStore {

    void save(String jti, long userId, long expiresInSeconds);

    boolean exists(String jti);

    void delete(String jti);
}

