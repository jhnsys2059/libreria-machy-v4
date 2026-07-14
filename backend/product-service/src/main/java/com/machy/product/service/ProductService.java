package com.machy.product.service;

import com.machy.product.dto.ProductRequest;
import com.machy.product.entity.Category;
import com.machy.product.entity.Product;
import com.machy.product.entity.Supplier;
import com.machy.product.repository.CategoryRepository;
import com.machy.product.repository.ProductRepository;
import com.machy.product.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAllByOrderByNombre();
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> findActive() {
        return productRepository.findByEstadoOrderByNombre("activo");
    }

    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Transactional
    public Product create(ProductRequest req) {
        if (productRepository.existsByCodigo(req.getCodigo())) {
            throw new RuntimeException("Codigo de barras ya registrado");
        }

        Product product = new Product();
        product.setCodigo(req.getCodigo());
        product.setNombre(req.getNombre());
        product.setDescripcion(req.getDescripcion() != null ? req.getDescripcion() : "");
        product.setCategoriaNombre(req.getCategoriaNombre() != null ? req.getCategoriaNombre() : "");
        product.setUnidad(req.getUnidad() != null ? req.getUnidad() : "unidad");
        product.setPrecioCompra(req.getPrecioCompra() != null ? req.getPrecioCompra() : BigDecimal.ZERO);
        product.setPrecioVenta(req.getPrecioVenta() != null ? req.getPrecioVenta() : BigDecimal.ZERO);
        product.setStock(req.getStock() != null ? req.getStock() : 0);
        product.setStockMinimo(req.getStockMinimo() != null ? req.getStockMinimo() : 5);
        product.setProveedorNombre(req.getProveedorNombre() != null ? req.getProveedorNombre() : "");
        product.setEstado("activo");

        if (req.getCategoriaId() != null) {
            categoryRepository.findById(req.getCategoriaId()).ifPresent(product::setCategoriaRel);
        }
        if (req.getProveedorId() != null) {
            supplierRepository.findById(req.getProveedorId()).ifPresent(product::setProveedorRel);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product update(UUID id, ProductRequest req) {
        Product product = findById(id);

        if (req.getCodigo() != null) product.setCodigo(req.getCodigo());
        if (req.getNombre() != null) product.setNombre(req.getNombre());
        if (req.getDescripcion() != null) product.setDescripcion(req.getDescripcion());
        if (req.getCategoriaNombre() != null) product.setCategoriaNombre(req.getCategoriaNombre());
        if (req.getUnidad() != null) product.setUnidad(req.getUnidad());
        if (req.getPrecioCompra() != null) product.setPrecioCompra(req.getPrecioCompra());
        if (req.getPrecioVenta() != null) product.setPrecioVenta(req.getPrecioVenta());
        if (req.getStock() != null) product.setStock(req.getStock());
        if (req.getStockMinimo() != null) product.setStockMinimo(req.getStockMinimo());
        if (req.getProveedorNombre() != null) product.setProveedorNombre(req.getProveedorNombre());

        if (req.getCategoriaId() != null) {
            categoryRepository.findById(req.getCategoriaId()).ifPresent(product::setCategoriaRel);
        }
        if (req.getProveedorId() != null) {
            supplierRepository.findById(req.getProveedorId()).ifPresent(product::setProveedorRel);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product toggleEstado(UUID id) {
        Product product = findById(id);
        product.setEstado("descontinuado".equals(product.getEstado()) ? "activo" : "descontinuado");
        return productRepository.save(product);
    }

    @Transactional
    public Product ajustarStock(UUID id, int cantidad) {
        Product product = findById(id);
        product.setStock(Math.max(0, product.getStock() + cantidad));
        return productRepository.save(product);
    }

    public List<Product> buscar(String q, String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return productRepository.buscarConCategoria(q, categoria);
        }
        return productRepository.buscar(q);
    }

    public Map<String, Object> getResumenInventario() {
        List<Product> activos = findActive();
        long alertas = activos.stream().filter(Product::isStockBajo).count();
        BigDecimal valor = activos.stream()
                .map(p -> p.getPrecioVenta().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> resumenCategorias = productRepository.resumenPorCategoria()
                .stream().map(row -> Map.of(
                        "categoria", row[0],
                        "productos", row[1],
                        "stock", row[2],
                        "valor", row[3]
                )).collect(Collectors.toList());

        return Map.of(
                "totalProductos", productRepository.count(),
                "productosActivos", (long) activos.size(),
                "valorInventario", valor,
                "alertasActivas", alertas,
                "resumenPorCategoria", resumenCategorias
        );
    }
}
