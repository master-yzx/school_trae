package com.campus.modules.auth.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.ResultCode;
import com.campus.modules.auth.dto.*;
import com.campus.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private void writeRefreshCookie(HttpServletResponse response, String token, long maxAgeSeconds) {
        if (response == null) return;
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token == null ? "" : token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private String readRefreshCookie(HttpServletRequest request) {
        if (request == null || request.getCookies() == null) return null;
        for (var c : request.getCookies()) {
            if ("refreshToken".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse resp = authService.login(request);
        if (resp == null) {
            return ApiResponse.failure(ResultCode.BUSINESS_ERROR, "账号、密码或验证码错误");
        }
        if (resp.getRefreshToken() != null) {
            writeRefreshCookie(response, resp.getRefreshToken(), resp.getRefreshExpiresIn());
            resp.setRefreshToken(null);
        }
        return ApiResponse.success(resp);
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String token = readRefreshCookie(request);
        if (token == null || token.isBlank()) {
            return ApiResponse.success(null);
        }
        RefreshTokenRequest dto = new RefreshTokenRequest();
        dto.setRefreshToken(token);
        LoginResponse resp = authService.refresh(dto);
        if (resp != null && resp.getRefreshToken() != null) {
            writeRefreshCookie(response, resp.getRefreshToken(), resp.getRefreshExpiresIn());
            resp.setRefreshToken(null);
        }
        return ApiResponse.success(resp);
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success();
    }

    @PostMapping("/captcha")
    public ApiResponse<Void> sendCaptcha(@Valid @RequestBody SendCaptchaRequest request) {
        authService.sendCaptcha(request);
        return ApiResponse.success();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = readRefreshCookie(request);
        if (token != null && !token.isBlank()) {
            RefreshTokenRequest dto = new RefreshTokenRequest();
            dto.setRefreshToken(token);
            authService.logout(dto);
        }
        // 清空 cookie
        writeRefreshCookie(response, "", 0);
        return ApiResponse.success();
    }

    @GetMapping("/check-phone")
    public ApiResponse<Boolean> checkPhone(@RequestParam String phone) {
        return ApiResponse.success(authService.checkPhoneExists(phone));
    }

  @GetMapping("/login-captcha")
  public ApiResponse<LoginCaptchaResponse> loginCaptcha() {
        return ApiResponse.success(authService.generateLoginCaptcha());
  }
}

