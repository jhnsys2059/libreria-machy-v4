package com.machy.product.controller;

import com.machy.product.entity.Category;
import com.machy.product.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of("success", true, "data", categoryService.findAll()));
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActive() {
        return ResponseEntity.ok(Map.of("success", true, "data", categoryService.findActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", categoryService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Category category) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", categoryService.create(category)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id, @RequestBody Category category) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", categoryService.update(id, category)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", categoryService.toggleStatus(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
