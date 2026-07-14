package com.machy.auth.security;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void invalidate(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isInvalidated(String token) {
        return blacklistedTokens.contains(token);
    }
}
