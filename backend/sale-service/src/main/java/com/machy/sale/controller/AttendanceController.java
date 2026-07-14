package com.machy.sale.controller;

import com.machy.sale.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(
            @RequestHeader("X-User-Id") String userId) {
        try {
            return ResponseEntity.ok(Map.of("success", true,
                "data", attendanceService.getStatusHoy(UUID.fromString(userId))));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false,
                "error", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    @PostMapping("/check-in")
    public ResponseEntity<Map<String, Object>> checkIn(
            @RequestHeader("X-User-Id") String userId) {
        try {
            return ResponseEntity.ok(Map.of("success", true,
                "data", attendanceService.marcarEntrada(UUID.fromString(userId))));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/check-out")
    public ResponseEntity<Map<String, Object>> checkOut(
            @RequestHeader("X-User-Id") String userId) {
        try {
            return ResponseEntity.ok(Map.of("success", true,
                "data", attendanceService.marcarSalida(UUID.fromString(userId))));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/weekly-report")
    public ResponseEntity<Map<String, Object>> getWeeklyReport() {
        return ResponseEntity.ok(Map.of("success", true,
            "data", attendanceService.getInformeSemanal()));
    }

    @GetMapping("/log")
    public ResponseEntity<Map<String, Object>> getLog() {
        return ResponseEntity.ok(Map.of("success", true,
            "data", attendanceService.getLogDetalle()));
    }

    @PostMapping("/admin")
    public ResponseEntity<Map<String, Object>> adminAttendance(
            @RequestBody Map<String, String> body) {
        try {
            String usuarioId = body.get("usuarioId");
            String tipo = body.get("tipo");
            if (usuarioId == null || tipo == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false,
                    "message", "usuarioId y tipo son requeridos"));
            }
            UUID uid = UUID.fromString(usuarioId);
            if ("entrada".equals(tipo)) {
                return ResponseEntity.ok(Map.of("success", true,
                    "data", attendanceService.marcarEntrada(uid)));
            } else if ("salida".equals(tipo)) {
                return ResponseEntity.ok(Map.of("success", true,
                    "data", attendanceService.marcarSalida(uid)));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false,
                    "message", "tipo debe ser 'entrada' o 'salida'"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false,
                "message", e.getMessage()));
        }
    }
}
