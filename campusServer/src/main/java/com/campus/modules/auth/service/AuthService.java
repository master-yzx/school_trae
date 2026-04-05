package com.campus.modules.auth.service;

import com.campus.modules.auth.dto.*;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);

    LoginCaptchaResponse generateLoginCaptcha();

    void register(RegisterRequest request);

    void sendCaptcha(SendCaptchaRequest request);

    void resetPassword(ResetPasswordRequest request);

    boolean checkPhoneExists(String phone);
}

