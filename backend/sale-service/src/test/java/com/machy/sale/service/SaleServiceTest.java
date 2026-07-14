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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Mock
    private LogRepository logRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private AuthClient authClient;

    @Captor
    private ArgumentCaptor<Sale> saleCaptor;

    @InjectMocks
    private SaleService saleService;

    @Test
    void createSale_success() {
        String vendedorId = UUID.randomUUID().toString();
        String vendedorNombre = "Juan Perez";
        String productoId = UUID.randomUUID().toString();

        SaleRequest.SaleItemRequest itemReq = new SaleRequest.SaleItemRequest();
        itemReq.setProductoId(productoId);
        itemReq.setCantidad(2);
        itemReq.setPrecioUnitario(new BigDecimal("10.00"));

        SaleRequest req = new SaleRequest();
        req.setItems(List.of(itemReq));
        req.setTotal(new BigDecimal("20.00"));
        req.setPagaCon(new BigDecimal("50.00"));

        Map<String, Object> productResponse = Map.of(
                "success", true,
                "data", Map.of(
                        "codigo", "PROD-001",
                        "nombre", "Lapiz HB",
                        "categoriaNombre", "Escritura",
                        "stock", 50
                )
        );

        when(productClient.getProductById(productoId)).thenReturn(productResponse);
        when(productClient.ajustarStock(eq(productoId), anyMap())).thenReturn(Map.of("success", true));
        when(saleRepository.maxNumero()).thenReturn(5);
        when(logRepository.save(any(LogEntry.class))).thenReturn(new LogEntry());

        Sale savedSale = new Sale();
        savedSale.setId(UUID.randomUUID());
        savedSale.setNumero(6);
        savedSale.setNumComp(6);
        savedSale.setVendedorId(UUID.fromString(vendedorId));
        savedSale.setVendedorNombre(vendedorNombre);
        savedSale.setSubtotal(new BigDecimal("20.00"));
        savedSale.setDescuento(BigDecimal.ZERO);
        savedSale.setTotal(new BigDecimal("20.00"));
        savedSale.setIgv(new BigDecimal("3.05"));
        savedSale.setCliente("VENTA AL CONTADO");
        savedSale.setClienteDni("");
        savedSale.setEstado("confirmada");
        savedSale.setBoleta(true);
        savedSale.setBoletaGenerada(true);
        savedSale.setPagaCon(new BigDecimal("50.00"));
        savedSale.setVuelto(new BigDecimal("30.00"));

        when(saleRepository.save(any(Sale.class))).thenReturn(savedSale);

        Sale result = saleService.create(req, vendedorId, vendedorNombre);

        assertNotNull(result);
        assertEquals(6, result.getNumero());
        assertEquals("confirmada", result.getEstado());
        assertEquals(vendedorNombre, result.getVendedorNombre());
        assertEquals("VENTA AL CONTADO", result.getCliente());

        verify(productClient).getProductById(productoId);
        verify(productClient).ajustarStock(eq(productoId), eq(Map.of("cantidad", -2)));
        verify(saleRepository).maxNumero();
        verify(logRepository).save(any(LogEntry.class));
        verify(saleRepository).save(saleCaptor.capture());

        Sale captured = saleCaptor.getValue();
        assertEquals(6, captured.getNumero());
        assertEquals(1, captured.getItems().size());
        assertEquals("VENTA AL CONTADO", captured.getCliente());
    }

    @Test
    void createSale_productNotFound() {
        String vendedorId = UUID.randomUUID().toString();
        String productoId = UUID.randomUUID().toString();

        SaleRequest.SaleItemRequest itemReq = new SaleRequest.SaleItemRequest();
        itemReq.setProductoId(productoId);
        itemReq.setCantidad(1);
        itemReq.setPrecioUnitario(new BigDecimal("5.00"));

        SaleRequest req = new SaleRequest();
        req.setItems(List.of(itemReq));

        when(productClient.getProductById(productoId)).thenReturn(Map.of("success", false));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> saleService.create(req, vendedorId, "Vendedor"));
        assertTrue(ex.getMessage().contains("Producto no encontrado"));
        verify(productClient).getProductById(productoId);
        verify(saleRepository, never()).save(any());
    }

    @Test
    void findAll_returnsSalesList() {
        Sale sale1 = new Sale();
        sale1.setId(UUID.randomUUID());
        sale1.setNumero(1);

        Sale sale2 = new Sale();
        sale2.setId(UUID.randomUUID());
        sale2.setNumero(2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Sale> salePage = new PageImpl<>(List.of(sale1, sale2), pageable, 2);

        when(saleRepository.findAll(pageable)).thenReturn(salePage);

        Page<Sale> result = saleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getContent().get(0).getNumero());
        assertEquals(2, result.getContent().get(1).getNumero());

        verify(saleRepository).findAll(pageable);
    }

    @Test
    void findAll_returnsEmptyList() {
        when(saleRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        List<Sale> result = saleService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(saleRepository).findAllByOrderByCreatedAtDesc();
    }
}
