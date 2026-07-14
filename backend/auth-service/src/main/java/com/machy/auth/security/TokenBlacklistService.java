package com.machy.auth.security;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);

    private final Map<String, Instant> blacklistedTokens;
    private final int maxSize;
    private final long tokenTtlMs;

    public TokenBlacklistService(
            @Value("${app.jwt.expiration-ms:28800000}") long tokenTtlMs,
            @Value("${app.blacklist.max-size:1000}") int maxSize) {
        this.tokenTtlMs = tokenTtlMs;
        this.maxSize = maxSize;
        this.blacklistedTokens = new LinkedHashMap<String, Instant>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Instant> eldest) {
                return size() > TokenBlacklistService.this.maxSize;
            }
        };
    }

    @PostConstruct
    public void init() {
        log.info("Token blacklist initialized: maxSize={}, ttlMs={}", maxSize, tokenTtlMs);
    }

    public synchronized void invalidate(String token) {
        blacklistedTokens.put(token, Instant.now().plusMillis(tokenTtlMs));
    }

    public synchronized boolean isInvalidated(String token) {
        Instant expiry = blacklistedTokens.get(token);
        if (expiry == null) return false;
        if (Instant.now().isAfter(expiry)) {
            blacklistedTokens.remove(token);
            return false;
        }
        return true;
    }

    @Scheduled(fixedRate = 600000)
    public synchronized void purgeExpired() {
        int before = blacklistedTokens.size();
        blacklistedTokens.values().removeIf(expiry -> Instant.now().isAfter(expiry));
        int after = blacklistedTokens.size();
        if (before != after) {
            log.info("Purged {} expired tokens ({} remaining)", before - after, after);
        }
    }
}
