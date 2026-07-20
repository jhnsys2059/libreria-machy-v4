package com.machy.auth.controller;

import com.machy.auth.dto.UserRequest;
import com.machy.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        if (page > 0 || size < 100) {
            return ResponseEntity.ok(Map.of("success", true,
                "data", userService.findAll(PageRequest.of(page, size, Sort.by("nombre")))));
        }
        return ResponseEntity.ok(Map.of("success", true, "data", userService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", userService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody UserRequest request) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", userService.create(request)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id, @RequestBody UserRequest request) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", userService.update(id, request)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", userService.toggleStatus(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
