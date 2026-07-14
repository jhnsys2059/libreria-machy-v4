package com.machy.sale.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthClientFallback implements FallbackFactory<AuthClient> {

    private static final Logger log = LoggerFactory.getLogger(AuthClientFallback.class);

    @Override
    public AuthClient create(Throwable cause) {
        log.error("Auth service fallback triggered: {}", cause.getMessage());
        return new AuthClient() {
            @Override
            public Map<String, Object> getUserById(String id) {
                return Map.of("success", false, "message", "Auth service no disponible");
            }

            @Override
            public Map<String, Object> getUserByUsername(String username) {
                return Map.of("success", false, "message", "Auth service no disponible");
            }
        };
    }
}
