package com.machy.sale.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class SaleRequest {
    private List<SaleItemRequest> items;

    @NotNull(message = "Total es requerido")
    private BigDecimal total;

    private BigDecimal descuento;
    private String tipoDescuento;
    private String cliente;
    private String clienteDni;
    private BigDecimal pagaCon;

    public SaleRequest() {}

    public List<SaleItemRequest> getItems() { return items; }
    public void setItems(List<SaleItemRequest> items) { this.items = items; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public String getTipoDescuento() { return tipoDescuento; }
    public void setTipoDescuento(String tipoDescuento) { this.tipoDescuento = tipoDescuento; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }
    public BigDecimal getPagaCon() { return pagaCon; }
    public void setPagaCon(BigDecimal pagaCon) { this.pagaCon = pagaCon; }

    public static class SaleItemRequest {
        private String productoId;
        private Integer cantidad;
        private BigDecimal precioUnitario;

        public SaleItemRequest() {}

        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    }
}
