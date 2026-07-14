package com.machy.product.service;

import com.machy.product.entity.Category;
import com.machy.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findActive() {
        return categoryRepository.findByActivoTrue();
    }

    public Category findById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }

    public Category create(Category category) {
        if (categoryRepository.existsByNombre(category.getNombre())) {
            throw new RuntimeException("Ya existe una categoria con ese nombre");
        }
        category.setActivo(true);
        return categoryRepository.save(category);
    }

    public Category update(UUID id, Category category) {
        Category existing = findById(id);
        if (category.getNombre() != null) existing.setNombre(category.getNombre());
        if (category.getDescripcion() != null) existing.setDescripcion(category.getDescripcion());
        return categoryRepository.save(existing);
    }

    public Category toggleStatus(UUID id) {
        Category category = findById(id);
        category.setActivo(!category.getActivo());
        return categoryRepository.save(category);
    }
}
