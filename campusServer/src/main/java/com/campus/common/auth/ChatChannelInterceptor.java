package com.campus.common.auth;

import com.campus.modules.auth.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class ChatChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    public ChatChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                try {
                    Claims claims = jwtService.parseClaims(token);
                    Object typ = claims.get(JwtService.CLAIM_TYP);
                    if (JwtService.TYP_ACCESS.equals(String.valueOf(typ))) {
                        long userId = Long.parseLong(claims.getSubject());
                        String role = String.valueOf(claims.get(JwtService.CLAIM_ROLE));
                        accessor.setUser(new JwtUserPrincipal(userId, role));
                    }
                } catch (Exception ex) {
                    throw new IllegalArgumentException("无效的聊天连接令牌");
                }
            } else {
                throw new IllegalArgumentException("未登录，无法建立聊天连接");
            }
        }
        return message;
    }
}

