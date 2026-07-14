# Libreria Machy SVM - Arquitectura de Microservicios v4.0

## Arquitectura General

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENTE (Frontend)                        │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    GATEWAY SERVICE (8080)                        │
│              Spring Cloud Gateway + CORS + Routing              │
└─────────────────────────────────────────────────────────────────┘
                                │
                ┌───────────────┼───────────────┐
                ▼               ▼               ▼
┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐
│   AUTH SERVICE    │ │ PRODUCT SERVICE   │ │   SALE SERVICE    │
│     (8081)        │ │     (8082)        │ │     (8083)        │
│                   │ │                   │ │                   │
│ - Usuarios        │ │ - Productos       │ │ - Ventas          │
│ - JWT Auth        │ │ - Categorias      │ │ - Asistencia      │
│ - Login/Logout    │ │ - Proveedores     │ │ - Reportes        │
│ - Recuperacion    │ │ - Inventario      │ │ - Escaneo         │
└───────────────────┘ └───────────────────┘ └───────────────────┘
        │                     │                     │
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                EUREKA DISCOVERY SERVICE (8761)                  │
│              Servidor de descubrimiento de servicios            │
└─────────────────────────────────────────────────────────────────┘
```

## Servicios

### 1. Discovery Service (Puerto 8761)
- **Funcion:** Servidor Eureka para descubrimiento automatico de servicios
- **Dependencia:** spring-cloud-starter-netflix-eureka-server
- **Configuracion:** No registra自身 (register-with-eureka: false)

### 2. Gateway Service (Puerto 8080)
- **Funcion:** API Gateway centralizado para enrutamiento
- **Dependencia:** spring-cloud-starter-gateway, eureka-client
- **Funcionalidades:**
  - Enrutamiento basado en rutas
  - Balanceo de carga con Eureka
  - CORS global configurado
  - Circuit Breaker con Resilience4j
  - Monitoreo con Actuator

### 3. Auth Service (Puerto 8081)
- **Funcion:** Autenticacion y gestion de usuarios
- **Dependencia:** spring-boot-starter-web, data-jpa, security, eureka-client
- **Endpoints:**
  - `POST /api/auth/login` - Inicio de sesion
  - `POST /api/auth/logout` - Cierre de sesion
  - `POST /api/auth/recuperar` - Recuperacion de contrasena
  - `GET /api/users` - Listar usuarios
  - `POST /api/users` - Crear usuario
  - `PUT /api/users/{id}` - Actualizar usuario
- **Base de Datos:** libreria_auth_db
- **Seguridad:** JWT con expiracion de 8 horas

### 4. Product Service (Puerto 8082)
- **Funcion:** Gestion de productos, categorias y proveedores
- **Dependencia:** spring-boot-starter-web, data-jpa, openfeign, eureka-client
- **Endpoints:**
  - `GET /api/products` - Listar productos
  - `POST /api/products` - Crear producto
  - `PUT /api/products/{id}` - Actualizar producto
  - `PATCH /api/products/{id}/toggle` - Cambiar estado
  - `PATCH /api/products/{id}/stock` - Ajustar stock
  - `GET /api/categories` - Listar categorias
  - `GET /api/suppliers` - Listar proveedores
- **Base de Datos:** libreria_product_db
- **Funcionalidad especial:** Resumen de inventario y alertas de stock

### 5. Sale Service (Puerto 8083)
- **Funcion:** Gestion de ventas, asistencia y reportes
- **Dependencia:** spring-boot-starter-web, data-jpa, openfeign, resilience4j, eureka-client
- **Endpoints:**
  - `GET /api/sales` - Listar ventas
  - `POST /api/sales` - Crear venta
  - `POST /api/sales/{id}/cancel` - Anular venta
  - `GET /api/sales/reports` - Reporte de ventas
  - `POST /api/attendance/check-in` - Marcar entrada
  - `POST /api/attendance/check-out` - Marcar salida
  - `GET /api/attendance/weekly-report` - Informe semanal
  - `POST /api/scan/session` - Crear sesion de escaneo
- **Base de Datos:** libreria_sale_db
- **Comunicacion inter-servicios:**
  - Feign Client -> Product Service (verificar stock, ajustar stock)
  - Feign Client -> Auth Service (obtener info de usuarios)

## Comunicacion entre Servicios

### Feign Clients
```java
// Product Client (en Sale Service)
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Map<String, Object> getProductById(@PathVariable String id);

    @PatchMapping("/api/products/{id}/stock")
    Map<String, Object> ajustarStock(@PathVariable String id, @RequestBody Map<String, Integer> body);
}

// Auth Client (en Sale Service)
@FeignClient(name = "auth-service")
public interface AuthClient {
    @GetMapping("/api/auth/user/id/{id}")
    Map<String, Object> getUserById(@PathVariable String id);
}
```

### Circuit Breaker (Resilience4j)
- **Proposito:** Proteccion contra fallos en cascada
- **Configuracion:**
  - Sliding Window: 10 llamadas
  - Umbral de fallo: 50%
  - Tiempo en estado abierto: 10 segundos
  - Llamadas permitidas en estado medio abierto: 3

## Base de Datos

Cada servicio tiene su propia base de datos PostgreSQL:

| Servicio | Base de Datos | Tablas Principales |
|----------|---------------|-------------------|
| Auth | libreria_auth_db | usuarios, logs |
| Product | libreria_product_db | productos, categorias, proveedores |
| Sale | libreria_sale_db | ventas, venta_items, asistencia, logs |

## Seguridad

### JWT (JSON Web Tokens)
- **Secret:** Configurado via variable de entorno `JWT_SECRET`
- **Expiracion:** 8 horas (28800000 ms)
- **Claims:** userId, username, rol

### Endpoints Publicos (sin autenticacion)
- `POST /api/auth/login`
- `POST /api/auth/recuperar`
- `GET /api/products/**` (lectura)
- `GET /api/categories/**` (lectura)
- `GET /api/suppliers/**` (lectura)

### Endpoints Protegidos
- `POST /api/users/**` (solo admin)
- `POST /api/products/**` (solo admin)
- `POST /api/sales/**` (autenticado)
- `POST /api/attendance/**` (autenticado)

## Variables de Entorno

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/libreria_auth_db
DB_USER=postgres
DB_PASS=postgres

# JWT
JWT_SECRET=una_clave_segura_muy_larga_para_firmar_tokens_deJWT_2024

# Server
PORT=8080
```

## Ejecucion

### 1. Crear Base de Datos
```sql
CREATE DATABASE libreria_auth_db;
CREATE DATABASE libreria_product_db;
CREATE DATABASE libreria_sale_db;
```

### 2. Iniciar Servicios (en orden)
```bash
# 1. Discovery Service
cd discovery-service
mvn spring-boot:run

# 2. Gateway Service
cd gateway-service
mvn spring-boot:run

# 3. Auth Service
cd auth-service
mvn spring-boot:run

# 4. Product Service
cd product-service
mvn spring-boot:run

# 5. Sale Service
cd sale-service
mvn spring-boot:run
```

### 3. Verificar Servicios
- Eureka Dashboard: http://localhost:8761
- Gateway: http://localhost:8080
- Auth API: http://localhost:8081/api/auth
- Product API: http://localhost:8082/api/products
- Sale API: http://localhost:8083/api/sales

## Usuarios por Defecto

| Usuario | Contrasena | Rol |
|---------|------------|-----|
| admin | admin123 | admin |
| ana | vendedor123 | vendedor |
| miguel | vendedor123 | vendedor |

## Tecnologias

- **Framework:** Spring Boot 3.4.4
- **Cloud:** Spring Cloud 2024.0.1
- **Java:** 17
- **Base de Datos:** PostgreSQL
- **Seguridad:** JWT (jjwt 0.12.6)
- **Comunicacion:** OpenFeign
- **Resiliencia:** Resilience4j
- **Descubrimiento:** Netflix Eureka
- **Gateway:** Spring Cloud Gateway
