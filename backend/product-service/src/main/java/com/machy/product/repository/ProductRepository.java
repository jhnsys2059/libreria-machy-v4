package com.machy.product.repository;

import com.machy.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Product> findByEstadoOrderByNombre(String estado);
    List<Product> findAllByOrderByNombre();

    @Query("SELECT p FROM Product p WHERE p.estado = 'activo' AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR p.codigo LIKE CONCAT('%', :q, '%'))")
    List<Product> buscar(String q);

    @Query("SELECT p FROM Product p WHERE p.estado = 'activo' AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR p.codigo LIKE CONCAT('%', :q, '%')) " +
           "AND (:categoria IS NULL OR p.categoriaNombre = :categoria)")
    List<Product> buscarConCategoria(String q, String categoria);

    @Query("SELECT p.categoriaNombre, COUNT(p), SUM(p.stock), SUM(p.precioVenta * p.stock) " +
           "FROM Product p WHERE p.estado = 'activo' GROUP BY p.categoriaNombre")
    List<Object[]> resumenPorCategoria();
}
