package com.machy.sale.repository;

import com.machy.sale.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findAllByOrderByCreatedAtDesc();
    List<Sale> findByVendedorIdOrderByCreatedAtDesc(UUID vendedorId);
    List<Sale> findByEstado(String estado);

    @Query("SELECT s FROM Sale s WHERE s.createdAt >= :since ORDER BY s.createdAt DESC")
    List<Sale> findRecent(Instant since);

    long countByEstadoAndCreatedAtBetween(String estado, Instant start, Instant end);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.estado = 'confirmada'")
    Double totalIngresos();

    @Query("SELECT MAX(s.numero) FROM Sale s")
    Integer maxNumero();
}
