package com.campus.modules.auth.service.impl;

import com.campus.modules.auth.dto.*;
import com.campus.modules.auth.service.AuthService;
import com.campus.modules.auth.jwt.JwtService;
import com.campus.modules.auth.jwt.RefreshTokenStore;
import com.campus.modules.auth.jwt.TokenPair;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.ResultCode;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserAccountRepository userAccountRepository;
    private final JwtService jwtService;
    private final RefreshTokenStore refreshTokenStore;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthServiceImpl(UserAccountRepository userAccountRepository,
                           JwtService jwtService,
                           RefreshTokenStore refreshTokenStore,
                           StringRedisTemplate stringRedisTemplate) {
        this.userAccountRepository = userAccountRepository;
        this.jwtService = jwtService;
        this.refreshTokenStore = refreshTokenStore;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 校验图形验证码
        if (request.getCaptchaId() == null || request.getCaptcha() == null) {
            return null;
        }
        String key = "login:captcha:" + request.getCaptchaId();
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached == null || !cached.equalsIgnoreCase(request.getCaptcha())) {
            return null;
        }
        stringRedisTemplate.delete(key);

        UserAccount user = userAccountRepository
                .findFirstByUsernameOrPhoneOrStudentNo(request.getAccount(), request.getAccount(), request.getAccount())
                .orElse(null);
        if (user == null) {
            return null;
        }

        // 账号被禁用，禁止登录
        if (user.getStatus() != null && !"NORMAL".equalsIgnoreCase(user.getStatus())) {
            // 使用业务异常 + 全局异常处理，返回统一的 ApiResponse，而不是 403 英文错误
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "账号已被禁用，请联系管理员");
        }

        if (!passwordMatches(request.getPassword(), user.getPassword())) {
            return null;
        }

        LoginResponse resp = new LoginResponse();
        TokenPair pair = jwtService.issueTokenPair(user.getId(), user.getRole());
        refreshTokenStore.save(pair.getRefreshJti(), user.getId(), pair.getRefreshExpiresIn());

        resp.setAccessToken(pair.getAccessToken());
        resp.setRefreshToken(pair.getRefreshToken());
        resp.setAccessExpiresIn(pair.getAccessExpiresIn());
        resp.setRefreshExpiresIn(pair.getRefreshExpiresIn());
        resp.setUserId(user.getId());
        resp.setNickname(user.getNickname() != null && !user.getNickname().isBlank() ? user.getNickname() : user.getUsername());
        resp.setRole(user.getRole() == null ? "USER" : user.getRole());
        return resp;
    }

    @Override
    public LoginResponse refresh(RefreshTokenRequest request) {
        Claims claims = jwtService.parseClaims(request.getRefreshToken());
        Object typ = claims.get(JwtService.CLAIM_TYP);
        if (!JwtService.TYP_REFRESH.equals(String.valueOf(typ))) {
            return null;
        }

        String jti = claims.getId();
        if (jti == null || !refreshTokenStore.exists(jti)) {
            return null;
        }

        long userId = Long.parseLong(claims.getSubject());
        UserAccount user = userAccountRepository.findById(userId).orElse(null);
        if (user == null) return null;

        // 轮换 refresh token：旧的作废，签发新的
        refreshTokenStore.delete(jti);
        TokenPair pair = jwtService.issueTokenPair(user.getId(), user.getRole());
        refreshTokenStore.save(pair.getRefreshJti(), user.getId(), pair.getRefreshExpiresIn());

        LoginResponse resp = new LoginResponse();
        resp.setAccessToken(pair.getAccessToken());
        resp.setRefreshToken(pair.getRefreshToken());
        resp.setAccessExpiresIn(pair.getAccessExpiresIn());
        resp.setRefreshExpiresIn(pair.getRefreshExpiresIn());
        resp.setUserId(user.getId());
        resp.setNickname(user.getNickname() != null && !user.getNickname().isBlank() ? user.getNickname() : user.getUsername());
        resp.setRole(user.getRole() == null ? "USER" : user.getRole());
        return resp;
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        try {
            Claims claims = jwtService.parseClaims(request.getRefreshToken());
            String jti = claims.getId();
            refreshTokenStore.delete(jti);
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void register(RegisterRequest request) {
        if (request == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "参数错误");
        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "手机号不能为空");
        }
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "两次密码不一致");
        }
        if (request.getRole() == null || request.getRole().isBlank()) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "角色不能为空");
        }
        // 同一手机号 / 用户名 / 学号 只能注册一次
        if (userAccountRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "手机号已注册");
        }
        String username = request.getUsername().trim();
        UserAccount existed = userAccountRepository
                .findFirstByUsernameOrPhoneOrStudentNo(username, request.getPhone(), request.getStudentNo())
                .orElse(null);
        if (existed != null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "账号已存在，请直接登录");
        }

        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPhone(request.getPhone());
        user.setStudentNo(request.getStudentNo());
        user.setNickname("用户" + request.getPhone().substring(Math.max(0, request.getPhone().length() - 4)));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCampusId(null);
        user.setRole(request.getRole());
        user.setStatus("NORMAL");
        user.setRegisterTime(LocalDateTime.now());
        userAccountRepository.save(user);
    }

    @Override
    public void sendCaptcha(SendCaptchaRequest request) {
        // TODO: 生成验证码并存入 Redis，同时发送短信/图形验证码
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "参数错误");
        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "手机号不能为空");
        }
        if (request.getNewPassword() == null || !request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "两次密码不一致");
        }
        UserAccount user = userAccountRepository.findFirstByPhone(request.getPhone()).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "手机号未注册");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userAccountRepository.save(user);
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        return userAccountRepository.existsByPhone(phone);
    }

    @Override
    public LoginCaptchaResponse generateLoginCaptcha() {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        String code = sb.toString();
        String id = UUID.randomUUID().toString();
        String key = "login:captcha:" + id;
        stringRedisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));

        LoginCaptchaResponse resp = new LoginCaptchaResponse();
        resp.setCaptchaId(id);

        // 生成带干扰的 SVG 图片验证码：字符轻微旋转/错位 + 噪点 + 干扰线
        StringBuilder svgBuilder = new StringBuilder();
        svgBuilder.append("<svg xmlns='http://www.w3.org/2000/svg' width='120' height='40'>");
        // 背景渐变
        svgBuilder.append("<defs>")
                .append("<linearGradient id='grad' x1='0%' y1='0%' x2='100%' y2='0%'>")
                .append("<stop offset='0%' style='stop-color:#f8fbff;stop-opacity:1'/>")
                .append("<stop offset='100%' style='stop-color:#e3e7f0;stop-opacity:1'/>")
                .append("</linearGradient>")
                .append("</defs>");
        svgBuilder.append("<rect width='120' height='40' fill='url(#grad)'/>");
        // 多条浅色斜线作为干扰线
        svgBuilder.append("<g stroke='#cbd5e1' stroke-width='1'>")
                .append("<line x1='0' y1='6' x2='120' y2='0'/>")
                .append("<line x1='0' y1='18' x2='120' y2='12'/>")
                .append("<line x1='0' y1='30' x2='120' y2='24'/>")
                .append("</g>");
        // 小噪点
        svgBuilder.append("<g fill='#94a3b8'>");
        for (int i = 0; i < 25; i++) {
            int px = (int) (Math.random() * 120);
            int py = (int) (Math.random() * 40);
            svgBuilder.append("<circle cx='").append(px).append("' cy='").append(py).append("' r='0.8'/>");
        }
        svgBuilder.append("</g>");
        // 每个字符单独变换：位置略微偏移、旋转、颜色不同
        int baseX = 18;
        int baseY = 26;
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            int offsetX = baseX + i * 22;
            int offsetY = baseY + (int) (Math.random() * 6 - 3); // -3 ~ 2 之间的轻微上下偏移
            int rotate = (int) (Math.random() * 30 - 15);        // -15° ~ 15° 旋转
            String color;
            switch (i) {
                case 0 -> color = "#1d4ed8";
                case 1 -> color = "#b91c1c";
                case 2 -> color = "#15803d";
                default -> color = "#92400e";
            }
            svgBuilder.append("<g transform='translate(")
                    .append(offsetX).append(",").append(offsetY)
                    .append(") rotate(").append(rotate).append(")'>")
                    .append("<text x='0' y='0' font-family='monospace' font-size='20' ")
                    .append("fill='").append(color).append("'>")
                    .append(ch)
                    .append("</text></g>");
        }
        svgBuilder.append("</svg>");

        String svg = svgBuilder.toString();
        String base64 = java.util.Base64.getEncoder()
                .encodeToString(svg.getBytes(StandardCharsets.UTF_8));
        resp.setImageData("data:image/svg+xml;base64," + base64);

        // 保留原始 code 字段（便于调试），前端不必使用
        resp.setCode(code);
        return resp;
    }

    private boolean passwordMatches(String raw, String stored) {
        if (raw == null || stored == null) return false;
        String s = stored.trim();

        if (s.startsWith("{bcrypt}")) {
            s = s.substring("{bcrypt}".length());
        }
        if (s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$")) {
            return passwordEncoder.matches(raw, s);
        }
        return raw.equals(stored);
    }
}

