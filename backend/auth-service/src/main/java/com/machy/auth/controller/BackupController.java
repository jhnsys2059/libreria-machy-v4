package com.machy.auth.controller;

import com.machy.auth.service.BackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/backup")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportData() {
        return ResponseEntity.ok(Map.of("success", true, "data", backupService.exportData()));
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importData(@RequestBody Map<String, Object> backup) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", backupService.importData(backup)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
