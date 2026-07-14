package com.machy.sale.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "venta_items")
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Sale venta;

    @Column(name = "producto_id")
    private UUID productoId;

    @Column(length = 50)
    private String codigo;

    @Column(name = "nombre_producto", length = 200)
    private String nombreProducto;

    @Column(length = 100)
    private String categoria;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public SaleItem() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    @JsonIgnore
    public Sale getVenta() { return venta; }
    public void setVenta(Sale venta) { this.venta = venta; }
    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }
}
