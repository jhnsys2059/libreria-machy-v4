package com.machy.sale.service;

import com.machy.sale.entity.Attendance;
import com.machy.sale.entity.Sale;
import com.machy.sale.entity.SaleItem;
import com.machy.sale.repository.AttendanceRepository;
import com.machy.sale.repository.SaleItemRepository;
import com.machy.sale.repository.SaleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class BackupService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final AttendanceRepository attendanceRepository;
    private final JdbcTemplate jdbcTemplate;

    public BackupService(SaleRepository saleRepository,
                         SaleItemRepository saleItemRepository,
                         AttendanceRepository attendanceRepository,
                         JdbcTemplate jdbcTemplate) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.attendanceRepository = attendanceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> exportData() {
        List<Map<String, Object>> salesList = new ArrayList<>();
        for (Sale s : saleRepository.findAll()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", s.getId());
            map.put("numero", s.getNumero());
            map.put("numComp", s.getNumComp());
            map.put("vendedorId", s.getVendedorId());
            map.put("vendedorNombre", s.getVendedorNombre());
            map.put("itemsJson", s.getItemsJson());
            map.put("subtotal", s.getSubtotal());
            map.put("descuento", s.getDescuento());
            map.put("total", s.getTotal());
            map.put("igv", s.getIgv());
            map.put("cliente", s.getCliente());
            map.put("clienteDni", s.getClienteDni());
            map.put("estado", s.getEstado());
            map.put("boleta", s.getBoleta());
            map.put("boletaGenerada", s.getBoletaGenerada());
            map.put("pagaCon", s.getPagaCon());
            map.put("vuelto", s.getVuelto());
            map.put("motivoAnulacion", s.getMotivoAnulacion());
            map.put("createdAt", s.getCreatedAt());
            map.put("updatedAt", s.getUpdatedAt());

            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (SaleItem si : saleItemRepository.findByVentaId(s.getId())) {
                Map<String, Object> imap = new LinkedHashMap<>();
                imap.put("id", si.getId());
                imap.put("productoId", si.getProductoId());
                imap.put("codigo", si.getCodigo());
                imap.put("nombreProducto", si.getNombreProducto());
                imap.put("categoria", si.getCategoria());
                imap.put("cantidad", si.getCantidad());
                imap.put("precioUnitario", si.getPrecioUnitario());
                imap.put("subtotal", si.getSubtotal());
                imap.put("createdAt", si.getCreatedAt());
                itemsList.add(imap);
            }
            map.put("items", itemsList);
            salesList.add(map);
        }

        List<Map<String, Object>> attendanceList = new ArrayList<>();
        for (Attendance a : attendanceRepository.findAll()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", a.getId());
            map.put("usuarioId", a.getUsuarioId());
            map.put("nombre", a.getNombre());
            map.put("fecha", a.getFecha());
            map.put("horaEntrada", a.getHoraEntrada());
            map.put("horaSalida", a.getHoraSalida());
            map.put("turno", a.getTurno());
            map.put("horas", a.getHoras());
            map.put("tardanzaMin", a.getTardanzaMin());
            map.put("cumpleTurno", a.getCumpleTurno());
            map.put("estadoAsistencia", a.getEstadoAsistencia());
            map.put("createdAt", a.getCreatedAt());
            attendanceList.add(map);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("version", "4.0.0");
        result.put("service", "sale-service");
        result.put("exportedAt", Instant.now());
        result.put("sales", salesList);
        result.put("attendance", attendanceList);
        return result;
    }

    @Transactional
    public Map<String, Object> importData(Map<String, Object> backup) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> salesData = (List<Map<String, Object>>) backup.get("sales");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> attendanceData = (List<Map<String, Object>>) backup.get("attendance");
        if ((salesData == null || salesData.isEmpty()) &&
            (attendanceData == null || attendanceData.isEmpty())) {
            Map<String, Object> empty = new LinkedHashMap<>();
            empty.put("salesImported", 0);
            empty.put("itemsImported", 0);
            empty.put("attendanceImported", 0);
            return empty;
        }

        jdbcTemplate.execute("DELETE FROM venta_items");
        jdbcTemplate.execute("DELETE FROM ventas");
        jdbcTemplate.execute("DELETE FROM asistencia");

        int saleCount = 0;
        int itemCount = 0;
        if (salesData != null) {
            for (Map<String, Object> sd : salesData) {
                UUID saleId = toUUID(sd.get("id")) != null ? toUUID(sd.get("id")) : UUID.randomUUID();
                jdbcTemplate.update(
                    "INSERT INTO ventas (id, numero, num_comp, vendedor_id, vendedor_nombre, items_json, subtotal, descuento, total, igv, cliente, cliente_dni, estado, boleta, boleta_generada, paga_con, vuelto, motivo_anulacion, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET numero=EXCLUDED.numero, num_comp=EXCLUDED.num_comp, vendedor_id=EXCLUDED.vendedor_id, vendedor_nombre=EXCLUDED.vendedor_nombre, items_json=EXCLUDED.items_json, subtotal=EXCLUDED.subtotal, descuento=EXCLUDED.descuento, total=EXCLUDED.total, igv=EXCLUDED.igv, cliente=EXCLUDED.cliente, cliente_dni=EXCLUDED.cliente_dni, estado=EXCLUDED.estado, boleta=EXCLUDED.boleta, boleta_generada=EXCLUDED.boleta_generada, paga_con=EXCLUDED.paga_con, vuelto=EXCLUDED.vuelto, motivo_anulacion=EXCLUDED.motivo_anulacion, updated_at=EXCLUDED.updated_at",
                    saleId,
                    sd.get("numero"), sd.get("numComp"),
                    toUUID(sd.get("vendedorId")), sd.get("vendedorNombre"),
                    sd.get("itemsJson"),
                    toBigDecimal(sd.get("subtotal")), toBigDecimal(sd.get("descuento")),
                    toBigDecimal(sd.get("total")), toBigDecimal(sd.get("igv")),
                    sd.get("cliente"), sd.get("clienteDni"), sd.get("estado"),
                    sd.get("boleta") != null ? sd.get("boleta") : false,
                    sd.get("boletaGenerada") != null ? sd.get("boletaGenerada") : false,
                    toBigDecimal(sd.get("pagaCon")), toBigDecimal(sd.get("vuelto")),
                    sd.get("motivoAnulacion"),
                    toTimestamp(sd.get("createdAt")), toTimestamp(sd.get("updatedAt")));
                saleCount++;

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> itemsData = (List<Map<String, Object>>) sd.get("items");
                if (itemsData != null) {
                    for (Map<String, Object> id : itemsData) {
                        jdbcTemplate.update(
                            "INSERT INTO venta_items (id, venta_id, producto_id, codigo, nombre_producto, categoria, cantidad, precio_unitario, subtotal, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            UUID.randomUUID(), saleId,
                            toUUID(id.get("productoId")), id.get("codigo"), id.get("nombreProducto"),
                            id.get("categoria"),
                            id.get("cantidad") != null ? ((Number) id.get("cantidad")).intValue() : 0,
                            toBigDecimal(id.get("precioUnitario")), toBigDecimal(id.get("subtotal")),
                            toTimestamp(id.get("createdAt")));
                        itemCount++;
                    }
                }
            }
        }

        int attCount = 0;
        if (attendanceData != null) {
            for (Map<String, Object> ad : attendanceData) {
                jdbcTemplate.update(
                    "INSERT INTO asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    UUID.randomUUID(),
                    toUUID(ad.get("usuarioId")), ad.get("nombre"),
                    ad.get("fecha") != null ? java.sql.Date.valueOf(ad.get("fecha").toString()) : null,
                    ad.get("horaEntrada") != null ? java.sql.Time.valueOf(LocalTime.parse(ad.get("horaEntrada").toString())) : null,
                    ad.get("horaSalida") != null ? java.sql.Time.valueOf(LocalTime.parse(ad.get("horaSalida").toString())) : null,
                    ad.get("turno"),
                    toBigDecimal(ad.get("horas")),
                    ad.get("tardanzaMin") != null ? ((Number) ad.get("tardanzaMin")).intValue() : null,
                    ad.get("cumpleTurno") != null ? ad.get("cumpleTurno") : null,
                    ad.get("estadoAsistencia"),
                    toTimestamp(ad.get("createdAt")));
                attCount++;
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("salesImported", saleCount);
        result.put("itemsImported", itemCount);
        result.put("attendanceImported", attCount);
        return result;
    }

    private UUID toUUID(Object val) {
        if (val == null) return null;
        if (val instanceof UUID) return (UUID) val;
        return UUID.fromString(val.toString());
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).doubleValue());
        return new BigDecimal(val.toString());
    }

    private java.sql.Timestamp toTimestamp(Object val) {
        if (val == null) return null;
        Instant instant = null;
        if (val instanceof Instant) instant = (Instant) val;
        else if (val instanceof String) instant = Instant.parse((String) val);
        return instant != null ? java.sql.Timestamp.from(instant) : null;
    }
}
