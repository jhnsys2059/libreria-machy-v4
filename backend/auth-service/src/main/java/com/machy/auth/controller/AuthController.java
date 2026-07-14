package com.machy.auth.controller;

import com.machy.auth.dto.LoginRequest;
import com.machy.auth.dto.LoginResponse;
import com.machy.auth.dto.PasswordRecoveryRequest;
import com.machy.auth.security.TokenBlacklistService;
import com.machy.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService blacklistService;

    public AuthController(AuthService authService, TokenBlacklistService blacklistService) {
        this.authService = authService;
        this.blacklistService = blacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(Map.of("success", true, "data", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/recover")
    public ResponseEntity<Map<String, Object>> recover(@Valid @RequestBody PasswordRecoveryRequest request) {
        try {
            var result = authService.recoverPassword(request);
            return ResponseEntity.ok(Map.of("success", true, "data", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            blacklistService.invalidate(authHeader.substring(7));
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Sesion cerrada correctamente"));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        try {
            var user = authService.findByUsername(username);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                    "id", user.getId().toString(),
                    "nombre", user.getNombre(),
                    "apellidos", user.getApellidos(),
                    "username", user.getUsername(),
                    "rol", user.getRol(),
                    "turno", user.getTurno(),
                    "activo", user.getActivo()
                )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/user/id/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String id) {
        try {
            var user = authService.findById(java.util.UUID.fromString(id));
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                    "id", user.getId().toString(),
                    "nombre", user.getNombre(),
                    "apellidos", user.getApellidos(),
                    "nombreCompleto", user.getNombreCompleto(),
                    "username", user.getUsername(),
                    "rol", user.getRol(),
                    "turno", user.getTurno(),
                    "activo", user.getActivo()
                )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
