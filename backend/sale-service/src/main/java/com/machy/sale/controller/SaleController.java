package com.machy.sale.controller;

import com.machy.sale.dto.SaleRequest;
import com.machy.sale.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Rol", required = false) String userRol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        try {
            boolean paginate = page > 0 || size < 100;
            if ("admin".equals(userRol)) {
                if (paginate) {
                    return ResponseEntity.ok(Map.of("success", true,
                        "data", saleService.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))));
                }
                return ResponseEntity.ok(Map.of("success", true, "data", saleService.findAll()));
            }
            if (userId != null) {
                UUID uid = UUID.fromString(userId);
                if (paginate) {
                    return ResponseEntity.ok(Map.of("success", true,
                        "data", saleService.findByVendedor(uid)));
                }
                return ResponseEntity.ok(Map.of("success", true, "data", saleService.findByVendedor(uid)));
            }
            return ResponseEntity.ok(Map.of("success", true, "data", saleService.findAll()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "ID de usuario invalido"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", saleService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody SaleRequest request,
            @RequestHeader(value = "X-User-Id") String userId,
            @RequestHeader(value = "X-User-Nombre", required = false) String userNombre) {
        try {
            if (userNombre == null || userNombre.isBlank()) {
                userNombre = saleService.resolveUserName(userId);
            }
            return ResponseEntity.ok(Map.of("success", true,
                "data", saleService.create(request, userId, userNombre)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancel(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") String userId) {
        try {
            String motivo = body.getOrDefault("motivo", "Sin motivo");
            return ResponseEntity.ok(Map.of("success", true,
                "data", saleService.anular(id, motivo, userId)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getReports() {
        return ResponseEntity.ok(Map.of("success", true, "data", saleService.getReporteVentas()));
    }
}
