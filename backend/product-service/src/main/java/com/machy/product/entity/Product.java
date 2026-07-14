package com.machy.product.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "productos")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT DEFAULT ''")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Category categoriaRel;

    @Column(name = "categoria", length = 100)
    private String categoriaNombre;

    @Column(length = 50)
    private String unidad;

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Supplier proveedorRel;

    @Column(name = "proveedor_nombre", length = 200)
    private String proveedorNombre;

    @Column(length = 20, nullable = false)
    private String estado;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Product() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Category getCategoriaRel() { return categoriaRel; }
    public void setCategoriaRel(Category categoriaRel) { this.categoriaRel = categoriaRel; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public Supplier getProveedorRel() { return proveedorRel; }
    public void setProveedorRel(Supplier proveedorRel) { this.proveedorRel = proveedorRel; }
    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public boolean isStockBajo() {
        return "activo".equals(estado) && stock <= stockMinimo;
    }

    public boolean isSinStock() {
        return "activo".equals(estado) && stock == 0;
    }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }
}
