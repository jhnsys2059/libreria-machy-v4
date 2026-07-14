package com.machy.sale.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScanService {

    private final Map<String, String> sessionPins = new ConcurrentHashMap<>();
    private final Map<String, Boolean> authenticatedSessions = new ConcurrentHashMap<>();

    public String createSession() {
        String sessionId = generateSessionId();
        String pin = String.format("%04d", (int) (Math.random() * 9000 + 1000));
        sessionPins.put(sessionId, pin);
        authenticatedSessions.put(sessionId, false);
        return sessionId;
    }

    public String getPin(String sessionId) {
        return sessionPins.get(sessionId);
    }

    public boolean verifyPin(String sessionId, String pin) {
        String expectedPin = sessionPins.get(sessionId);
        if (expectedPin != null && expectedPin.equals(pin)) {
            authenticatedSessions.put(sessionId, true);
            return true;
        }
        return false;
    }

    public boolean isAuthenticated(String sessionId) {
        return authenticatedSessions.getOrDefault(sessionId, false);
    }

    public void endSession(String sessionId) {
        sessionPins.remove(sessionId);
        authenticatedSessions.remove(sessionId);
    }

    private String generateSessionId() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }
}
