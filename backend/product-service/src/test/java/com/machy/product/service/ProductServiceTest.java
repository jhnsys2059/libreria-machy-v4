package com.machy.product.service;

import com.machy.product.dto.ProductRequest;
import com.machy.product.entity.Category;
import com.machy.product.entity.Product;
import com.machy.product.entity.Supplier;
import com.machy.product.repository.CategoryRepository;
import com.machy.product.repository.ProductRepository;
import com.machy.product.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @InjectMocks
    private ProductService productService;

    private Product createSampleProduct() {
        Product p = new Product();
        p.setId(UUID.randomUUID());
        p.setCodigo("PROD-001");
        p.setNombre("Lapiz HB");
        p.setDescripcion("Lapiz de grafito");
        p.setCategoriaNombre("Escritura");
        p.setUnidad("unidad");
        p.setPrecioCompra(new BigDecimal("0.50"));
        p.setPrecioVenta(new BigDecimal("1.50"));
        p.setStock(100);
        p.setStockMinimo(10);
        p.setProveedorNombre("Proveedor XYZ");
        p.setEstado("activo");
        return p;
    }

    @Test
    void findAll_returnsAllProducts() {
        Product p1 = createSampleProduct();
        Product p2 = createSampleProduct();
        p2.setId(UUID.randomUUID());
        p2.setCodigo("PROD-002");
        p2.setNombre("Borrador");

        when(productRepository.findAllByOrderByNombre()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Lapiz HB", result.get(0).getNombre());
        assertEquals("Borrador", result.get(1).getNombre());
        verify(productRepository).findAllByOrderByNombre();
    }

    @Test
    void findById_productExists() {
        Product product = createSampleProduct();
        UUID id = product.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product result = productService.findById(id);

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getNombre(), result.getNombre());
        verify(productRepository).findById(id);
    }

    @Test
    void findById_productNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.findById(id));
        assertEquals("Producto no encontrado", ex.getMessage());
        verify(productRepository).findById(id);
    }

    @Test
    void createProduct_success() {
        ProductRequest req = new ProductRequest();
        req.setCodigo("PROD-003");
        req.setNombre("Cuaderno A4");
        req.setDescripcion("Cuaderno universitario");
        req.setCategoriaNombre("Papeleria");
        req.setUnidad("unidad");
        req.setPrecioCompra(new BigDecimal("3.00"));
        req.setPrecioVenta(new BigDecimal("6.50"));
        req.setStock(50);
        req.setStockMinimo(5);
        req.setProveedorNombre("Distribuidora ABC");

        Product savedProduct = new Product();
        savedProduct.setId(UUID.randomUUID());
        savedProduct.setCodigo("PROD-003");
        savedProduct.setNombre("Cuaderno A4");
        savedProduct.setDescripcion("Cuaderno universitario");
        savedProduct.setCategoriaNombre("Papeleria");
        savedProduct.setUnidad("unidad");
        savedProduct.setPrecioCompra(new BigDecimal("3.00"));
        savedProduct.setPrecioVenta(new BigDecimal("6.50"));
        savedProduct.setStock(50);
        savedProduct.setStockMinimo(5);
        savedProduct.setProveedorNombre("Distribuidora ABC");
        savedProduct.setEstado("activo");

        when(productRepository.existsByCodigo("PROD-003")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.create(req);

        assertNotNull(result);
        assertEquals("PROD-003", result.getCodigo());
        assertEquals("Cuaderno A4", result.getNombre());
        assertEquals("activo", result.getEstado());

        verify(productRepository).existsByCodigo("PROD-003");
        verify(productRepository).save(productCaptor.capture());
        Product captured = productCaptor.getValue();
        assertEquals("PROD-003", captured.getCodigo());
        assertEquals("Cuaderno A4", captured.getNombre());
        assertEquals("Papeleria", captured.getCategoriaNombre());
    }

    @Test
    void createProduct_duplicateCode_throwsException() {
        ProductRequest req = new ProductRequest();
        req.setCodigo("EXISTING-CODE");
        req.setNombre("Producto Duplicado");

        when(productRepository.existsByCodigo("EXISTING-CODE")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.create(req));
        assertEquals("Codigo de barras ya registrado", ex.getMessage());
        verify(productRepository, never()).save(any());
    }
}
