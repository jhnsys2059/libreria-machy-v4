package com.machy.sale.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ventas")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer numero;

    @Column(name = "num_comp")
    private Integer numComp;

    @Column(name = "vendedor_id")
    private UUID vendedorId;

    @Column(length = 200)
    private String vendedorNombre;

    @Column(columnDefinition = "TEXT")
    private String itemsJson = "[]";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descuento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(length = 200)
    private String cliente;

    @Column(name = "cliente_dni", length = 20)
    private String clienteDni;

    @Column(length = 20)
    private String estado;

    @Column(nullable = false)
    private Boolean boleta;

    @Column(name = "boleta_generada")
    private Boolean boletaGenerada;

    @Column(name = "paga_con", precision = 10, scale = 2)
    private BigDecimal pagaCon;

    @Column(precision = 10, scale = 2)
    private BigDecimal vuelto;

    @Column(name = "motivo_anulacion", columnDefinition = "TEXT DEFAULT ''")
    private String motivoAnulacion;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Sale() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getNumComp() { return numComp; }
    public void setNumComp(Integer numComp) { this.numComp = numComp; }
    public UUID getVendedorId() { return vendedorId; }
    public void setVendedorId(UUID vendedorId) { this.vendedorId = vendedorId; }
    public String getVendedorNombre() { return vendedorNombre; }
    public void setVendedorNombre(String vendedorNombre) { this.vendedorNombre = vendedorNombre; }
    public String getItemsJson() { return itemsJson; }
    public void setItemsJson(String itemsJson) { this.itemsJson = itemsJson; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Boolean getBoleta() { return boleta; }
    public void setBoleta(Boolean boleta) { this.boleta = boleta; }
    public Boolean getBoletaGenerada() { return boletaGenerada; }
    public void setBoletaGenerada(Boolean boletaGenerada) { this.boletaGenerada = boletaGenerada; }
    public BigDecimal getPagaCon() { return pagaCon; }
    public void setPagaCon(BigDecimal pagaCon) { this.pagaCon = pagaCon; }
    public BigDecimal getVuelto() { return vuelto; }
    public void setVuelto(BigDecimal vuelto) { this.vuelto = vuelto; }
    public String getMotivoAnulacion() { return motivoAnulacion; }
    public void setMotivoAnulacion(String motivoAnulacion) { this.motivoAnulacion = motivoAnulacion; }
    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }
}
