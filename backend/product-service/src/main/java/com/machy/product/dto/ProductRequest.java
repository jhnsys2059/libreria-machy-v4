package com.machy.product.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductRequest {
    @NotBlank(message = "Codigo es requerido")
    private String codigo;

    @NotBlank(message = "Nombre es requerido")
    private String nombre;

    private String descripcion;
    private UUID categoriaId;
    private String categoriaNombre;
    private String unidad;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stock;
    private Integer stockMinimo;
    private UUID proveedorId;
    private String proveedorNombre;

    public ProductRequest() {}

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public UUID getCategoriaId() { return categoriaId; }
    public void setCategoriaId(UUID categoriaId) { this.categoriaId = categoriaId; }
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
    public UUID getProveedorId() { return proveedorId; }
    public void setProveedorId(UUID proveedorId) { this.proveedorId = proveedorId; }
    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
}
