package com.machy.sale.service;

import com.machy.sale.client.AuthClient;
import com.machy.sale.client.ProductClient;
import com.machy.sale.dto.SaleRequest;
import com.machy.sale.entity.LogEntry;
import com.machy.sale.entity.Sale;
import com.machy.sale.entity.SaleItem;
import com.machy.sale.repository.LogRepository;
import com.machy.sale.repository.SaleItemRepository;
import com.machy.sale.repository.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final LogRepository logRepository;
    private final ProductClient productClient;
    private final AuthClient authClient;

    public SaleService(SaleRepository saleRepository, SaleItemRepository saleItemRepository,
                       LogRepository logRepository, ProductClient productClient, AuthClient authClient) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.logRepository = logRepository;
        this.productClient = productClient;
        this.authClient = authClient;
    }

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");
    private static final BigDecimal IGV_DIVISOR = new BigDecimal("1.18");

    public List<Sale> findAll() {
        return saleRepository.findAllByOrderByCreatedAtDesc();
    }

    public Page<Sale> findAll(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

    public List<Sale> findByVendedor(UUID vendedorId) {
        return saleRepository.findByVendedorIdOrderByCreatedAtDesc(vendedorId);
    }

    public Sale findById(UUID id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    public String resolveUserName(String userId) {
        try {
            Map<String, Object> resp = authClient.getUserById(userId);
            if (Boolean.TRUE.equals(resp.get("success"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) resp.get("data");
                String nombre = (String) data.getOrDefault("nombre", "");
                String apellidos = (String) data.getOrDefault("apellidos", "");
                return (nombre + " " + apellidos).trim();
            }
        } catch (Exception ignored) {}
        return "Vendedor";
    }

    @Transactional
    public Sale create(SaleRequest req, String vendedorId, String vendedorNombre) {
        BigDecimal subtotal = BigDecimal.ZERO;
        List<SaleItem> saleItems = new ArrayList<>();

        for (var itemReq : req.getItems()) {
            var productResponse = productClient.getProductById(itemReq.getProductoId());
            if (Boolean.FALSE.equals(productResponse.get("success"))) {
                throw new RuntimeException("Producto no encontrado: " + itemReq.getProductoId());
            }

            var productData = (Map<?, ?>) productResponse.get("data");
            int stockActual = (Integer) productData.get("stock");
            if (stockActual < itemReq.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + productData.get("nombre"));
            }

            BigDecimal itemSubtotal = itemReq.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(itemReq.getCantidad()));
            subtotal = subtotal.add(itemSubtotal);

            productClient.ajustarStock(itemReq.getProductoId(),
                    Map.of("cantidad", -itemReq.getCantidad()));

            SaleItem item = new SaleItem();
            item.setProductoId(UUID.fromString(itemReq.getProductoId()));
            item.setCodigo((String) productData.get("codigo"));
            item.setNombreProducto((String) productData.get("nombre"));
            item.setCategoria((String) productData.get("categoriaNombre"));
            item.setCantidad(itemReq.getCantidad());
            item.setPrecioUnitario(itemReq.getPrecioUnitario());
            item.setSubtotal(itemSubtotal);
            saleItems.add(item);
        }

        BigDecimal descuento = req.getDescuento() != null ? req.getDescuento() : BigDecimal.ZERO;
        if ("pct".equals(req.getTipoDescuento())) {
            descuento = subtotal.multiply(descuento).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        BigDecimal total = subtotal.subtract(descuento);
        BigDecimal igv = total.multiply(IGV_RATE).divide(IGV_DIVISOR, 2, RoundingMode.HALF_UP);
        BigDecimal pagaCon = req.getPagaCon() != null ? req.getPagaCon() : BigDecimal.ZERO;
        BigDecimal vuelto = pagaCon.compareTo(total) >= 0 ? pagaCon.subtract(total) : BigDecimal.ZERO;
        boolean emiteBoleta = total.compareTo(new BigDecimal("5.00")) >= 0;

        Integer nextNum = saleRepository.maxNumero();
        int num = (nextNum != null ? nextNum : 0) + 1;

        Sale sale = new Sale();
        sale.setNumero(num);
        sale.setNumComp(num);
        sale.setVendedorId(UUID.fromString(vendedorId));
        sale.setVendedorNombre(vendedorNombre);
        sale.setSubtotal(subtotal);
        sale.setDescuento(descuento);
        sale.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        sale.setIgv(igv.setScale(2, RoundingMode.HALF_UP));
        sale.setCliente(req.getCliente() != null ? req.getCliente() : "VENTA AL CONTADO");
        sale.setClienteDni(req.getClienteDni() != null ? req.getClienteDni() : "");
        sale.setEstado("confirmada");
        sale.setBoleta(emiteBoleta);
        sale.setBoletaGenerada(emiteBoleta);
        sale.setPagaCon(pagaCon);
        sale.setVuelto(vuelto);

        saleItems.forEach(item -> item.setVenta(sale));
        sale.setItems(saleItems);

        logRepository.save(LogEntry.builder()
                .nivel("info").modulo("ventas")
                .mensaje("Venta #" + num + " confirmada por " + vendedorNombre)
                .usuarioId(UUID.fromString(vendedorId))
                .build());

        return saleRepository.save(sale);
    }

    @Transactional
    public Sale anular(UUID id, String motivo, String adminId) {
        Sale sale = findById(id);
        if (!"confirmada".equals(sale.getEstado())) {
            throw new RuntimeException("La venta no esta en estado confirmada");
        }

        sale.setEstado("anulada");
        sale.setMotivoAnulacion(motivo);

        for (SaleItem item : sale.getItems()) {
            productClient.ajustarStock(item.getProductoId().toString(),
                    Map.of("cantidad", item.getCantidad()));
        }

        logRepository.save(LogEntry.builder()
                .nivel("info").modulo("ventas")
                .mensaje("Venta #" + sale.getNumero() + " anulada: " + motivo)
                .usuarioId(UUID.fromString(adminId))
                .build());

        return saleRepository.save(sale);
    }

    public Map<String, Object> getReporteVentas() {
        List<Sale> confirmadas = saleRepository.findByEstado("confirmada");
        BigDecimal ingresos = confirmadas.stream()
                .map(Sale::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        long boletas = confirmadas.stream().filter(Sale::getBoleta).count();
        BigDecimal ticketPromedio = confirmadas.isEmpty() ? BigDecimal.ZERO
                : ingresos.divide(BigDecimal.valueOf(confirmadas.size()), 2, RoundingMode.HALF_UP);

        List<Object[]> top = saleItemRepository.topProductos();
        List<Map<String, Object>> topProductos = top.stream().limit(8).map(row -> Map.of(
                "nombre", row[0], "categoria", row[1],
                "unidades", row[2], "ingresos", row[3]
        )).collect(Collectors.toList());

        ZoneId lima = ZoneId.of("America/Lima");
        List<Map<String, Object>> ventasPorDia = new ArrayList<>();
        String[] dias = {"Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom"};
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now(lima).minusDays(i);
            BigDecimal total = confirmadas.stream()
                    .filter(s -> {
                        Instant c = s.getCreatedAt();
                        return c != null && c.atZone(lima).toLocalDate().equals(d);
                    })
                    .map(Sale::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            ventasPorDia.add(Map.of("label", dias[(d.getDayOfWeek().getValue() + 6) % 7],
                    "valor", total, "dia", d.toString()));
        }

        return Map.of(
                "ventasConfirmadas", confirmadas.size(),
                "ingresosTotales", ingresos,
                "ticketPromedio", ticketPromedio,
                "boletasEmitidas", boletas,
                "topProductos", topProductos,
                "ventasPorDia", ventasPorDia
        );
    }
}
