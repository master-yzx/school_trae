package com.campus.modules.auth.jwt;

import com.campus.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtService {

    public static final String CLAIM_TYP = "typ";
    public static final String TYP_ACCESS = "access";
    public static final String TYP_REFRESH = "refresh";
    public static final String CLAIM_ROLE = "role";

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        byte[] bytes = props.getSecret() == null ? new byte[0] : props.getSecret().getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public TokenPair issueTokenPair(long userId, String role) {
        Instant now = Instant.now();

        String accessJti = UUID.randomUUID().toString();
        Instant accessExp = now.plusSeconds(props.getAccessTtlSeconds());
        String access = Jwts.builder()
                .issuer(props.getIssuer())
                .subject(String.valueOf(userId))
                .id(accessJti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(accessExp))
                .claims(Map.of(
                        CLAIM_TYP, TYP_ACCESS,
                        CLAIM_ROLE, role == null ? "USER" : role
                ))
                .signWith(key)
                .compact();

        String refreshJti = UUID.randomUUID().toString();
        Instant refreshExp = now.plusSeconds(props.getRefreshTtlSeconds());
        String refresh = Jwts.builder()
                .issuer(props.getIssuer())
                .subject(String.valueOf(userId))
                .id(refreshJti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(refreshExp))
                .claims(Map.of(CLAIM_TYP, TYP_REFRESH))
                .signWith(key)
                .compact();

        return new TokenPair(access, refresh,
                props.getAccessTtlSeconds(), props.getRefreshTtlSeconds(),
                accessJti, refreshJti);
    }

    public Claims parseClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(props.getIssuer())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}

