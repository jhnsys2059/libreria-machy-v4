package com.machy.sale.repository;

import com.machy.sale.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
    List<SaleItem> findByVentaId(UUID ventaId);

    @Query("SELECT si.nombreProducto, si.categoria, SUM(si.cantidad), SUM(si.subtotal) " +
           "FROM SaleItem si JOIN si.venta s WHERE s.estado = 'confirmada' " +
           "GROUP BY si.nombreProducto, si.categoria ORDER BY SUM(si.cantidad) DESC")
    List<Object[]> topProductos();
}
