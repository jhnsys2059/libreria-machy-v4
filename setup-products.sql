-- ============================================================
-- SETUP PRODUCTS - Libreria Machy SVM v4.0 Microservices
-- Ejecutar en NeonDB SQL Editor
-- ============================================================

-- Productos de ejemplo para la libreria
-- Usa subqueries para obtener category_id y proveedor_id automaticamente

INSERT INTO product_schema.productos (
    id, codigo, nombre, descripcion, categoria_id, categoria, unidad,
    precio_compra, precio_venta, stock, stock_minimo,
    proveedor_id, proveedor_nombre, estado, created_at, updated_at
) VALUES

-- 1. Cuaderno Espiral A4 100h
(gen_random_uuid(), '7501234567890', 'Cuaderno Espiral A4 100h',
 'Cuaderno universitario tapa dura',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Utiles escolares' LIMIT 1),
 'Utiles escolares', 'unidad', 4.50, 7.90, 45, 10,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Distribuidora Escolar SAC' LIMIT 1),
 'Distribuidora Escolar SAC', 'activo', NOW(), NOW()),

-- 2. Lapicero BIC Cristal Azul x12
(gen_random_uuid(), '7501234567891', 'Lapicero BIC Cristal Azul x12',
 'Caja 12 lapiceros punta media',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Utiles escolares' LIMIT 1),
 'Utiles escolares', 'caja', 8.00, 13.50, 3, 8,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'BIC Perú' LIMIT 1),
 'BIC Perú', 'activo', NOW(), NOW()),

-- 3. Resma Papel Bond A4 75g
(gen_random_uuid(), '7501234567892', 'Resma Papel Bond A4 75g',
 '500 hojas papel bond',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Papelería' LIMIT 1),
 'Papelería', 'paquete', 18.00, 26.90, 22, 5,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Paperworld' LIMIT 1),
 'Paperworld', 'activo', NOW(), NOW()),

-- 4. Temperas Faber-Castell x12
(gen_random_uuid(), '7501234567893', 'Témperas Faber-Castell x12',
 'Set 12 colores no tóxico',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Manualidades' LIMIT 1),
 'Manualidades', 'unidad', 6.50, 11.90, 2, 5,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Faber-Castell Perú' LIMIT 1),
 'Faber-Castell Perú', 'activo', NOW(), NOW()),

-- 5. Harry Potter T.1
(gen_random_uuid(), '7501234567894', 'Harry Potter T.1',
 'La Piedra Filosofal tapa blanda',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Libros' LIMIT 1),
 'Libros', 'unidad', 22.00, 39.90, 8, 3,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Salamandra' LIMIT 1),
 'Salamandra', 'activo', NOW(), NOW()),

-- 6. Tijeras Maped 17cm
(gen_random_uuid(), '7501234567895', 'Tijeras Maped 17cm',
 'Punta roma de seguridad',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Utiles escolares' LIMIT 1),
 'Utiles escolares', 'unidad', 3.20, 5.50, 18, 6,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Maped Perú' LIMIT 1),
 'Maped Perú', 'activo', NOW(), NOW()),

-- 7. Rompecabezas 500 piezas (sin stock)
(gen_random_uuid(), '7501234567896', 'Rompecabezas 500 piezas',
 'Juguete educativo naturaleza',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Juguetes' LIMIT 1),
 'Juguetes', 'unidad', 15.00, 29.90, 0, 3,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Ravensburger' LIMIT 1),
 'Ravensburger', 'activo', NOW(), NOW()),

-- 8. Regla 30cm Maped Clear
(gen_random_uuid(), '7501234567897', 'Regla 30cm Maped Clear',
 'Transparente doble escala',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Utiles escolares' LIMIT 1),
 'Utiles escolares', 'unidad', 1.50, 2.90, 35, 10,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Maped Perú' LIMIT 1),
 'Maped Perú', 'activo', NOW(), NOW()),

-- 9. Marcadores Plumones x24
(gen_random_uuid(), '7501234567898', 'Marcadores Plumones x24',
 'Set 24 colores lavables',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Manualidades' LIMIT 1),
 'Manualidades', 'unidad', 9.00, 16.90, 12, 4,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Artesco Perú' LIMIT 1),
 'Artesco Perú', 'activo', NOW(), NOW()),

-- 10. Globo Terráqueo 20cm (descontinuado)
(gen_random_uuid(), '7501234567899', 'Globo Terráqueo 20cm',
 'Base giratoria escolar',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Utiles escolares' LIMIT 1),
 'Utiles escolares', 'unidad', 28.00, 49.90, 4, 2,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Ediciones SM' LIMIT 1),
 'Ediciones SM', 'descontinuado', NOW(), NOW()),

-- 11. Plastilina Faber 12 colores
(gen_random_uuid(), '7501234567900', 'Plastilina Faber 12 colores',
 'No tóxica blanda',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Manualidades' LIMIT 1),
 'Manualidades', 'unidad', 4.00, 7.50, 25, 8,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Faber-Castell Perú' LIMIT 1),
 'Faber-Castell Perú', 'activo', NOW(), NOW()),

-- 12. Calculadora Casio FX-82LA (stock bajo)
(gen_random_uuid(), '7501234567901', 'Calculadora Casio FX-82LA',
 '240 funciones científica',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Utiles escolares' LIMIT 1),
 'Utiles escolares', 'unidad', 35.00, 58.90, 1, 3,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Casio Perú' LIMIT 1),
 'Casio Perú', 'activo', NOW(), NOW()),

-- 13. Goma en Barra Pritt 43g
(gen_random_uuid(), '7501234567902', 'Goma en Barra Pritt 43g',
 'Sin disolventes',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Utiles escolares' LIMIT 1),
 'Utiles escolares', 'unidad', 3.50, 6.20, 30, 8,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Distribuidora Escolar SAC' LIMIT 1),
 'Distribuidora Escolar SAC', 'activo', NOW(), NOW()),

-- 14. Cartulina Iris A2 x10
(gen_random_uuid(), '7501234567903', 'Cartulina Iris A2 x10',
 'Colores surtidos',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Papelería' LIMIT 1),
 'Papelería', 'paquete', 5.00, 9.50, 14, 4,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Paperworld' LIMIT 1),
 'Paperworld', 'activo', NOW(), NOW()),

-- 15. Compás Maped Study
(gen_random_uuid(), '7501234567904', 'Compás Maped Study',
 'Con lápiz incluido',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Utiles escolares' LIMIT 1),
 'Utiles escolares', 'unidad', 5.50, 9.90, 7, 3,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Maped Perú' LIMIT 1),
 'Maped Perú', 'activo', NOW(), NOW()),

-- 16. Pintura Acuarela 24 Colores (sin stock)
(gen_random_uuid(), '7501234567905', 'Pintura Acuarela 24 Colores',
 'Pastillas lavables',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Manualidades' LIMIT 1),
 'Manualidades', 'unidad', 8.00, 14.90, 0, 5,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Faber-Castell Perú' LIMIT 1),
 'Faber-Castell Perú', 'activo', NOW(), NOW()),

-- 17. Folder Manila A4 x50
(gen_random_uuid(), '7501234567906', 'Folder Manila A4 x50',
 'Apertura lateral',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Papelería' LIMIT 1),
 'Papelería', 'paquete', 6.00, 10.90, 20, 5,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Paperworld' LIMIT 1),
 'Paperworld', 'activo', NOW(), NOW()),

-- 18. Lápiz HB Faber x12
(gen_random_uuid(), '7501234567907', 'Lápiz HB Faber x12',
 'Grafito hexagonales',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Utiles escolares' LIMIT 1),
 'Utiles escolares', 'caja', 4.00, 7.20, 40, 10,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Faber-Castell Perú' LIMIT 1),
 'Faber-Castell Perú', 'activo', NOW(), NOW()),

-- 19. Bloques Lego 200pzs (stock bajo)
(gen_random_uuid(), '7501234567908', 'Bloques Lego 200pzs',
 'Construcción 6+ años',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Juguetes' LIMIT 1),
 'Juguetes', 'unidad', 45.00, 79.90, 3, 2,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Ravensburger' LIMIT 1),
 'Ravensburger', 'activo', NOW(), NOW()),

-- 20. Libro Colorear Mandalas
(gen_random_uuid(), '7501234567909', 'Libro Colorear Mandalas',
 '60 páginas bond 90g',
 (SELECT id FROM product_schema.categorias WHERE nombre = 'Libros' LIMIT 1),
 'Libros', 'unidad', 7.00, 12.90, 9, 3,
 (SELECT id FROM product_schema.proveedores WHERE nombre = 'Ediciones SM' LIMIT 1),
 'Ediciones SM', 'activo', NOW(), NOW());

-- Verificar insercion
SELECT 'Productos insertados: ' || COUNT(*)::text AS resultado
FROM product_schema.productos;
