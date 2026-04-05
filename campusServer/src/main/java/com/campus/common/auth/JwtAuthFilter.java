package com.campus.common.auth;

import com.campus.modules.auth.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthFilter extends HttpFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /** 公开接口：token 无效时也不返回 401，照常放行，避免首页/列表等拿不到数据 */
    private static boolean isPublicPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) return false;
        if (path.startsWith("/api/auth/")) return true;
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            if (path.startsWith("/api/banners")) return true;
            if (path.startsWith("/api/products/")) return true;   // 推荐/搜索/详情等均可匿名
            if (path.startsWith("/api/campuses")) return true;
            if (path.startsWith("/api/forum/posts") && !path.matches(".*/posts/\\d+.*")) return true; // 列表可匿名
            if (path.startsWith("/api/notice")) return true;
        }
        return false;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7).trim();
            if (!token.isEmpty()) {
                try {
                    Claims claims = jwtService.parseClaims(token);
                    Object typ = claims.get(JwtService.CLAIM_TYP);
                    if (JwtService.TYP_ACCESS.equals(String.valueOf(typ))) {
                        long userId = Long.parseLong(claims.getSubject());
                        String role = String.valueOf(claims.get(JwtService.CLAIM_ROLE));
                        try {
                            CurrentUserHolder.set(new CurrentUser(userId, role));
                            chain.doFilter(request, response);
                            return;
                        } finally {
                            CurrentUserHolder.clear();
                        }
                    }
                } catch (Exception e) {
                    // Token 无效或过期：仅对「需要登录」的接口返回 401，公开接口照常放行
                    if (!isPublicPath(request)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"code\":401,\"message\":\"登录已过期，请重新登录\"}");
                        return;
                    }
                }
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            CurrentUserHolder.clear();
        }
    }
}

