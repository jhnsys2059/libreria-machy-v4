package com.machy.product.controller;

import com.machy.product.dto.ProductRequest;
import com.machy.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(Map.of("success", true, "data", productService.buscar(q, categoria)));
        }
        if (page > 0 || size < 100) {
            return ResponseEntity.ok(Map.of("success", true,
                "data", productService.findAll(PageRequest.of(page, size, Sort.by("nombre")))));
        }
        return ResponseEntity.ok(Map.of("success", true, "data", productService.findAll()));
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActive() {
        return ResponseEntity.ok(Map.of("success", true, "data", productService.findActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", productService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/by-code/{codigo}")
    public ResponseEntity<Map<String, Object>> getByCode(@PathVariable String codigo) {
        var products = productService.findAll().stream()
                .filter(p -> p.getCodigo().equals(codigo)).findFirst();
        if (products.isPresent()) {
            return ResponseEntity.ok(Map.of("success", true, "data", products.get()));
        }
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Producto no encontrado"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ProductRequest request) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", productService.create(request)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id,
                                                      @Valid @RequestBody ProductRequest request) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", productService.update(id, request)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", productService.toggleEstado(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> ajustarStock(@PathVariable UUID id,
                                                            @RequestBody Map<String, Integer> body) {
        try {
            int cantidad = body.getOrDefault("cantidad", 0);
            return ResponseEntity.ok(Map.of("success", true, "data", productService.ajustarStock(id, cantidad)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/inventory-summary")
    public ResponseEntity<Map<String, Object>> getInventorySummary() {
        return ResponseEntity.ok(Map.of("success", true, "data", productService.getResumenInventario()));
    }
}
