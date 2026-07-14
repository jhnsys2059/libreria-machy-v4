package com.machy.sale.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScanService {

    private static final Logger log = LoggerFactory.getLogger(ScanService.class);

    private final Map<String, SessionInfo> sessions;
    private final long sessionTtlMs;
    private final int maxSessions;

    public ScanService(
            @Value("${app.scan.session-ttl-ms:3600000}") long sessionTtlMs,
            @Value("${app.scan.max-sessions:100}") int maxSessions) {
        this.sessionTtlMs = sessionTtlMs;
        this.maxSessions = maxSessions;
        this.sessions = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        log.info("Scan service initialized: maxSessions={}, sessionTtlMs={}", maxSessions, sessionTtlMs);
    }

    public String createSession() {
        if (sessions.size() >= maxSessions) {
            log.warn("Max sessions reached ({}), rejecting new session", maxSessions);
            return null;
        }
        String sessionId = generateSessionId();
        String pin = String.format("%04d", (int) (Math.random() * 9000 + 1000));
        sessions.put(sessionId, new SessionInfo(pin, Instant.now()));
        log.info("Session created: {} (total: {})", sessionId, sessions.size());
        return sessionId;
    }

    public String getPin(String sessionId) {
        SessionInfo info = sessions.get(sessionId);
        return info != null ? info.pin() : null;
    }

    public boolean verifyPin(String sessionId, String pin) {
        SessionInfo info = sessions.get(sessionId);
        if (info != null && info.pin().equals(pin)) {
            sessions.put(sessionId, info.withAuthenticated());
            return true;
        }
        return false;
    }

    public boolean isAuthenticated(String sessionId) {
        SessionInfo info = sessions.get(sessionId);
        return info != null && info.authenticated();
    }

    public void endSession(String sessionId) {
        sessions.remove(sessionId);
        log.info("Session ended: {} (remaining: {})", sessionId, sessions.size());
    }

    @Scheduled(fixedRate = 300000)
    public void purgeExpired() {
        int before = sessions.size();
        Instant cutoff = Instant.now().minusMillis(sessionTtlMs);
        Iterator<Map.Entry<String, SessionInfo>> it = sessions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, SessionInfo> entry = it.next();
            if (entry.getValue().createdAt().isBefore(cutoff)) {
                it.remove();
            }
        }
        int after = sessions.size();
        if (before != after) {
            log.info("Purged {} expired sessions ({} remaining)", before - after, after);
        }
    }

    private String generateSessionId() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    private record SessionInfo(String pin, Instant createdAt, boolean authenticated) {
        SessionInfo(String pin, Instant createdAt) {
            this(pin, createdAt, false);
        }
        SessionInfo withAuthenticated() {
            return new SessionInfo(pin, createdAt, true);
        }
    }
}
