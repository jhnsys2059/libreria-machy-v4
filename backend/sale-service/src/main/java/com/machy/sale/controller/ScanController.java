package com.machy.sale.controller;

import com.machy.sale.service.ScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scan")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/session")
    public ResponseEntity<Map<String, Object>> createSession(
            @RequestHeader(value = "Host", required = false) String host,
            @RequestHeader(value = "X-Forwarded-Proto", required = false) String forwardedProto) {
        String sessionId = scanService.createSession();
        if (sessionId == null) {
            return ResponseEntity.status(503).body(Map.of("success", false, "message", "Maximo de sesiones alcanzado. Intenta mas tarde."));
        }
        String pin = scanService.getPin(sessionId);

        String protocol = "https".equals(forwardedProto) ? "https" : "http";
        String baseUrl = protocol + "://" + (host != null ? host : "localhost:8080");
        String scanUrl = baseUrl + "/remote-scan.html?session=" + sessionId;
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=" +
                java.net.URLEncoder.encode(scanUrl, java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                        "sessionId", sessionId,
                        "pin", pin,
                        "scanUrl", scanUrl,
                        "qrUrl", qrUrl
                )
        ));
    }

    @PostMapping("/session/{sessionId}/verify-pin")
    public ResponseEntity<Map<String, Object>> verifyPin(@PathVariable String sessionId,
                                                         @RequestBody Map<String, String> body) {
        String pin = body.get("pin");
        if (pin == null || pin.length() != 4) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "PIN invalido"));
        }
        boolean ok = scanService.verifyPin(sessionId, pin);
        if (ok) {
            return ResponseEntity.ok(Map.of("success", true, "data", Map.of("authenticated", true, "message", "PIN correcto")));
        }
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("authenticated", false, "message", "PIN incorrecto")));
    }

    @PostMapping("/session/{sessionId}/code")
    public ResponseEntity<Map<String, Object>> submitCode(@PathVariable String sessionId,
                                                          @RequestBody Map<String, String> body) {
        if (!scanService.isAuthenticated(sessionId)) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Sesion no autenticada"));
        }
        String code = body.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Codigo requerido"));
        }
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("code", code)));
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> endSession(@PathVariable String sessionId) {
        scanService.endSession(sessionId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Sesion finalizada"));
    }
}
