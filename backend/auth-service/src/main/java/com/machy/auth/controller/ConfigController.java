package com.machy.auth.controller;

import com.machy.auth.service.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> get() {
        return ResponseEntity.ok(Map.of("success", true, "data", configService.getAll()));
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody Map<String, String> data) {
        return ResponseEntity.ok(Map.of("success", true, "data", configService.saveAll(data)));
    }
}
