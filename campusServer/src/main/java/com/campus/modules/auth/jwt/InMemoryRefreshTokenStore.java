package com.campus.modules.auth.jwt;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryRefreshTokenStore implements RefreshTokenStore {

    private static class Entry {
        final long userId;
        final long expEpochSeconds;

        Entry(long userId, long expEpochSeconds) {
            this.userId = userId;
            this.expEpochSeconds = expEpochSeconds;
        }
    }

    private final Map<String, Entry> map = new ConcurrentHashMap<>();

    @Override
    public void save(String jti, long userId, long expiresInSeconds) {
        long exp = Instant.now().getEpochSecond() + Math.max(1, expiresInSeconds);
        map.put(jti, new Entry(userId, exp));
    }

    @Override
    public boolean exists(String jti) {
        Entry e = map.get(jti);
        if (e == null) return false;
        long now = Instant.now().getEpochSecond();
        if (e.expEpochSeconds <= now) {
            map.remove(jti);
            return false;
        }
        return true;
    }

    @Override
    public void delete(String jti) {
        if (jti == null) return;
        map.remove(jti);
    }
}

