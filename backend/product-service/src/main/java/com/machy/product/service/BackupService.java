package com.machy.product.service;

import com.machy.product.entity.Category;
import com.machy.product.entity.Product;
import com.machy.product.entity.Supplier;
import com.machy.product.repository.CategoryRepository;
import com.machy.product.repository.ProductRepository;
import com.machy.product.repository.SupplierRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class BackupService {

    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;

    public BackupService(CategoryRepository categoryRepository,
                         SupplierRepository supplierRepository,
                         ProductRepository productRepository,
                         JdbcTemplate jdbcTemplate) {
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> exportData() {
        List<Map<String, Object>> categoriesList = new ArrayList<>();
        for (Category c : categoryRepository.findAll()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", c.getId());
            map.put("nombre", c.getNombre());
            map.put("descripcion", c.getDescripcion());
            map.put("activo", c.getActivo());
            map.put("createdAt", c.getCreatedAt());
            map.put("updatedAt", c.getUpdatedAt());
            categoriesList.add(map);
        }

        List<Map<String, Object>> suppliersList = new ArrayList<>();
        for (Supplier s : supplierRepository.findAll()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", s.getId());
            map.put("nombre", s.getNombre());
            map.put("ruc", s.getRuc());
            map.put("contacto", s.getContacto());
            map.put("telefono", s.getTelefono());
            map.put("email", s.getEmail());
            map.put("direccion", s.getDireccion());
            map.put("activo", s.getActivo());
            map.put("createdAt", s.getCreatedAt());
            map.put("updatedAt", s.getUpdatedAt());
            suppliersList.add(map);
        }

        List<Map<String, Object>> productsList = new ArrayList<>();
        for (Product p : productRepository.findAll()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", p.getId());
            map.put("codigo", p.getCodigo());
            map.put("nombre", p.getNombre());
            map.put("descripcion", p.getDescripcion());
            map.put("categoriaNombre", p.getCategoriaNombre());
            map.put("unidad", p.getUnidad());
            map.put("precioCompra", p.getPrecioCompra());
            map.put("precioVenta", p.getPrecioVenta());
            map.put("stock", p.getStock());
            map.put("stockMinimo", p.getStockMinimo());
            map.put("proveedorNombre", p.getProveedorNombre());
            map.put("estado", p.getEstado());
            map.put("createdAt", p.getCreatedAt());
            map.put("updatedAt", p.getUpdatedAt());
            productsList.add(map);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("version", "4.0.0");
        result.put("service", "product-service");
        result.put("exportedAt", Instant.now());
        result.put("categories", categoriesList);
        result.put("suppliers", suppliersList);
        result.put("products", productsList);
        return result;
    }

    @Transactional
    public Map<String, Object> importData(Map<String, Object> backup) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> categoriesData = (List<Map<String, Object>>) backup.get("categories");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> suppliersData = (List<Map<String, Object>>) backup.get("suppliers");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> productsData = (List<Map<String, Object>>) backup.get("products");
        if ((categoriesData == null || categoriesData.isEmpty()) &&
            (suppliersData == null || suppliersData.isEmpty()) &&
            (productsData == null || productsData.isEmpty())) {
            throw new RuntimeException("El backup no contiene datos de productos, categorías o proveedores");
        }

        jdbcTemplate.execute("DELETE FROM productos");
        jdbcTemplate.execute("DELETE FROM proveedores");
        jdbcTemplate.execute("DELETE FROM categorias");

        int catCount = 0;
        if (categoriesData != null) {
            for (Map<String, Object> cd : categoriesData) {
                jdbcTemplate.update(
                    "INSERT INTO categorias (id, nombre, descripcion, activo, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET nombre=EXCLUDED.nombre, descripcion=EXCLUDED.descripcion, activo=EXCLUDED.activo, updated_at=EXCLUDED.updated_at",
                    toUUID(cd.get("id")), cd.get("nombre"), cd.get("descripcion"),
                    cd.get("activo") != null ? cd.get("activo") : true,
                    toTimestamp(cd.get("createdAt")), toTimestamp(cd.get("updatedAt")));
                catCount++;
            }
        }

        int supCount = 0;
        if (suppliersData != null) {
            for (Map<String, Object> sd : suppliersData) {
                jdbcTemplate.update(
                    "INSERT INTO proveedores (id, nombre, ruc, contacto, telefono, email, direccion, activo, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET nombre=EXCLUDED.nombre, ruc=EXCLUDED.ruc, contacto=EXCLUDED.contacto, telefono=EXCLUDED.telefono, email=EXCLUDED.email, direccion=EXCLUDED.direccion, activo=EXCLUDED.activo, updated_at=EXCLUDED.updated_at",
                    toUUID(sd.get("id")), sd.get("nombre"), sd.get("ruc"), sd.get("contacto"),
                    sd.get("telefono"), sd.get("email"), sd.get("direccion"),
                    sd.get("activo") != null ? sd.get("activo") : true,
                    toTimestamp(sd.get("createdAt")), toTimestamp(sd.get("updatedAt")));
                supCount++;
            }
        }

        int prodCount = 0;
        if (productsData != null) {
            for (Map<String, Object> pd : productsData) {
                jdbcTemplate.update(
                    "INSERT INTO productos (id, codigo, nombre, descripcion, categoria, unidad, precio_compra, precio_venta, stock, stock_minimo, proveedor_nombre, estado, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET codigo=EXCLUDED.codigo, nombre=EXCLUDED.nombre, descripcion=EXCLUDED.descripcion, categoria=EXCLUDED.categoria, unidad=EXCLUDED.unidad, precio_compra=EXCLUDED.precio_compra, precio_venta=EXCLUDED.precio_venta, stock=EXCLUDED.stock, stock_minimo=EXCLUDED.stock_minimo, proveedor_nombre=EXCLUDED.proveedor_nombre, estado=EXCLUDED.estado, updated_at=EXCLUDED.updated_at",
                    toUUID(pd.get("id")), pd.get("codigo"), pd.get("nombre"), pd.get("descripcion"),
                    pd.get("categoriaNombre"), pd.get("unidad"),
                    toBigDecimal(pd.get("precioCompra")), toBigDecimal(pd.get("precioVenta")),
                    pd.get("stock") != null ? ((Number) pd.get("stock")).intValue() : 0,
                    pd.get("stockMinimo") != null ? ((Number) pd.get("stockMinimo")).intValue() : 0,
                    pd.get("proveedorNombre"), pd.get("estado"),
                    toTimestamp(pd.get("createdAt")), toTimestamp(pd.get("updatedAt")));
                prodCount++;
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("categoriesImported", catCount);
        result.put("suppliersImported", supCount);
        result.put("productsImported", prodCount);
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
