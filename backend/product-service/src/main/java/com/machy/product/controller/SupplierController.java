package com.machy.product.controller;

import com.machy.product.entity.Supplier;
import com.machy.product.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of("success", true, "data", supplierService.findAll()));
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActive() {
        return ResponseEntity.ok(Map.of("success", true, "data", supplierService.findActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", supplierService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Supplier supplier) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", supplierService.create(supplier)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id, @RequestBody Supplier supplier) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", supplierService.update(id, supplier)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", supplierService.toggleStatus(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
