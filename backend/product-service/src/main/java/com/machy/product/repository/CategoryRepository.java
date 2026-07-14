package com.machy.product.repository;

import com.machy.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByActivoTrue();
    boolean existsByNombre(String nombre);
}
