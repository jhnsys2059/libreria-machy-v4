package com.machy.sale.controller;

import com.machy.sale.repository.SaleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final SaleRepository saleRepository;

    public DashboardController(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        var confirmadas = saleRepository.findByEstado("confirmada");
        BigDecimal ingresos = confirmadas.stream()
                .map(s -> s.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
        long ventasHoy = confirmadas.stream()
                .filter(s -> s.getCreatedAt() != null
                        && s.getCreatedAt().atZone(ZoneId.of("America/Lima")).toLocalDate().equals(hoy))
                .count();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                        "ventasHoy", ventasHoy,
                        "ventasTotales", confirmadas.size(),
                        "ingresosTotales", ingresos
                )
        ));
    }
}
