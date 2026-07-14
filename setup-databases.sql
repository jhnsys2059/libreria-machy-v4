-- ============================================
-- Script SQL para crear bases de datos en NeonDB
-- ============================================
-- INSTRUCCIONES:
-- 1. Ve a https://neon.tech y crea una cuenta
-- 2. Crea un proyecto nuevo
-- 3. En el SQL Editor de Neon, ejecuta este script
-- 4. Copia la URL de conexion de cada base de datos
-- ============================================

-- NeonDB ya tiene una base de datos por defecto (neondb)
-- Necesitas crear las 3 bases de datos separadas

-- NOTA: En NeonDB serverless, no puedes crear databases directamente
-- desde el SQL Editor. En su lugar, debes:
-- 1. Usar la base de datos "neondb" por defecto
-- 2. O crear databases desde la consola de Neon

-- ALTERNATIVA: Usar una sola base de datos con esquemas separados
-- (esto es lo que haremos aqui)

-- Crear esquemas para cada servicio
CREATE SCHEMA IF NOT EXISTS auth_schema;
CREATE SCHEMA IF NOT EXISTS product_schema;
CREATE SCHEMA IF NOT EXISTS sale_schema;

-- ============================================
-- TABLAS DEL AUTH SERVICE (esquema: auth_schema)
-- ============================================
SET search_path TO auth_schema;

CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(8) UNIQUE,
    telefono VARCHAR(20),
    correo VARCHAR(200) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    rol VARCHAR(20),
    turno VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT true,
    intentos_fallidos INTEGER DEFAULT 0,
    bloqueado_hasta TIMESTAMP WITH TIME ZONE,
    ultimo_acceso TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nivel VARCHAR(20),
    modulo VARCHAR(100),
    mensaje TEXT NOT NULL,
    usuario_id UUID REFERENCES auth_schema.usuarios(id),
    contexto TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ============================================
-- TABLAS DEL PRODUCT SERVICE (esquema: product_schema)
-- ============================================
SET search_path TO product_schema;

CREATE TABLE IF NOT EXISTS categorias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT DEFAULT '',
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS proveedores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(200) NOT NULL,
    ruc VARCHAR(11),
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion TEXT DEFAULT '',
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS productos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT DEFAULT '',
    categoria_id UUID REFERENCES product_schema.categorias(id),
    categoria VARCHAR(100),
    unidad VARCHAR(50),
    precio_compra NUMERIC(10,2) NOT NULL DEFAULT 0,
    precio_venta NUMERIC(10,2) NOT NULL DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0,
    stock_minimo INTEGER NOT NULL DEFAULT 5,
    proveedor_id UUID REFERENCES product_schema.proveedores(id),
    proveedor_nombre VARCHAR(200),
    estado VARCHAR(20) NOT NULL DEFAULT 'activo',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ============================================
-- TABLAS DEL SALE SERVICE (esquema: sale_schema)
-- ============================================
SET search_path TO sale_schema;

CREATE TABLE IF NOT EXISTS ventas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero INTEGER,
    num_comp INTEGER,
    vendedor_id UUID,
    vendedor_nombre VARCHAR(200),
    items_json TEXT DEFAULT '[]',
    subtotal NUMERIC(10,2) NOT NULL DEFAULT 0,
    descuento NUMERIC(10,2) NOT NULL DEFAULT 0,
    total NUMERIC(10,2) NOT NULL DEFAULT 0,
    igv NUMERIC(10,2) NOT NULL DEFAULT 0,
    cliente VARCHAR(200),
    cliente_dni VARCHAR(20),
    estado VARCHAR(20),
    boleta BOOLEAN NOT NULL DEFAULT false,
    boleta_generada BOOLEAN DEFAULT false,
    paga_con NUMERIC(10,2),
    vuelto NUMERIC(10,2),
    motivo_anulacion TEXT DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS venta_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venta_id UUID REFERENCES sale_schema.ventas(id),
    producto_id UUID,
    codigo VARCHAR(50),
    nombre_producto VARCHAR(200),
    categoria VARCHAR(100),
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(10,2) NOT NULL DEFAULT 0,
    subtotal NUMERIC(10,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS asistencia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID,
    nombre VARCHAR(200),
    fecha DATE NOT NULL,
    hora_entrada TIME,
    hora_salida TIME,
    turno VARCHAR(20),
    horas NUMERIC(5,2),
    tardanza_min INTEGER,
    cumple_turno BOOLEAN DEFAULT false,
    estado_asistencia VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nivel VARCHAR(20),
    modulo VARCHAR(100),
    mensaje TEXT NOT NULL,
    usuario_id UUID,
    contexto TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ============================================
-- DATOS INICIALES
-- ============================================

-- Usuarios (auth_schema)
SET search_path TO auth_schema;
INSERT INTO usuarios (nombre, apellidos, dni, correo, username, password_hash, rol, turno, activo) VALUES
('Jhon', 'Taipe', '00000000', 'admin@machy.com', 'admin', '$2a$12$LJ3m4ys3Gz8H.6F8Q9X5K.O8X5F8Q9X5K.O8X5F8Q9X5K.O8X5F8', 'admin', 'completo', true),
('Ana', 'Flores', '11111111', 'ana@machy.com', 'ana', '$2a$12$LJ3m4ys3Gz8H.6F8Q9X5K.O8X5F8Q9X5K.O8X5F8Q9X5K.O8X5F8', 'vendedor', 'completo', true),
('Miguel', 'Torres', '22222222', 'miguel@machy.com', 'miguel', '$2a$12$LJ3m4ys3Gz8H.6F8Q9X5K.O8X5F8Q9X5K.O8X5F8Q9X5K.O8X5F8', 'vendedor', 'tarde', true)
ON CONFLICT (username) DO NOTHING;

-- Categorias (product_schema)
SET search_path TO product_schema;
INSERT INTO categorias (nombre, descripcion, activo) VALUES
('Utiles escolares', 'Cuadernos, lapiceros, lapices, colores y mas', true),
('Papeleria', 'Hojas, sobres, folders, resmas y articulos de oficina', true),
('Libros', 'Textos escolares, novelas y material de lectura', true),
('Manualidades', 'Tijeras, goma, escarcha, cartulinas y mas', true),
('Juguetes', 'Juguetes educativos y recreativos', true),
('Otros', 'Productos varios', true)
ON CONFLICT (nombre) DO NOTHING;

-- Proveedores (product_schema)
INSERT INTO proveedores (nombre, ruc, contacto, telefono, email, direccion, activo) VALUES
('Distribuidora ABC', '20123456789', 'Carlos Lopez', '999888777', 'abc@proveedores.com', 'Av. Principal 123', true),
('Papeles del Peru', '20987654321', 'Maria Garcia', '987654321', 'papeles@proveedores.com', 'Jr. Comercio 456', true),
('Libros Mundo SAC', '20456789123', 'Pedro Sanchez', '976543210', 'libros@mundo.com', 'Calle Real 789', true)
ON CONFLICT DO NOTHING;

-- ============================================
-- CONFIRMACION
-- ============================================
SELECT 'Bases de datos creadas correctamente!' as resultado;
SELECT 'auth_schema: ' || COUNT(*) || ' usuarios' FROM auth_schema.usuarios;
SELECT 'product_schema: ' || COUNT(*) || ' categorias' FROM product_schema.categorias;
SELECT 'product_schema: ' || COUNT(*) || ' proveedores' FROM product_schema.proveedores;
