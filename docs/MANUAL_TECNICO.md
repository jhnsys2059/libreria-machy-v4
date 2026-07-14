# Manual Técnico — Librería Machy v4.0

## 1. Tecnologías Utilizadas

| Tecnología | Versión |
|---|---|
| Java | 17 (Eclipse Temurin) |
| Spring Boot | 3.4.4 |
| Spring Cloud | 2024.0.1 |
| Spring Cloud Gateway | — |
| Spring Cloud Netflix Eureka | — |
| Spring Cloud OpenFeign | — |
| Resilience4j | — |
| PostgreSQL | 16 |
| JWT (jjwt) | 0.12.6 |
| BCrypt | — |
| WebSocket (STOMP) | — |
| Docker | 24+ |
| Docker Compose | 2.x |
| Railway | Cloud deployment |
| Maven | 3.8+ |

## 2. Arquitectura

### 2.1 Descripción de los Microservicios

El sistema está compuesto por 5 microservicios más un API Gateway y un Service Discovery:

| Servicio | Puerto | Descripción |
|---|---|---|
| **discovery-service** | 8761 | Servidor Eureka para registro y descubrimiento de servicios |
| **gateway-service** | 8080 | API Gateway con Spring Cloud Gateway, enrutamiento y CORS |
| **auth-service** | 8081 | Autenticación JWT, gestión de usuarios, recuperación de contraseña |
| **product-service** | 8082 | Catálogo de productos, categorías, proveedores y stock |
| **sale-service** | 8083 | Ventas, asistencia, escaneo remoto y dashboard |

### 2.2 Diagrama de Arquitectura

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
│ - JWT Auth        │ │ - Categorías      │ │ - Asistencia      │
│ - Login/Logout    │ │ - Proveedores     │ │ - Reportes        │
│ - Recuperación    │ │ - Inventario      │ │ - Escaneo         │
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

### 2.3 Tabla de Enrutamiento (Gateway)

| Ruta | Servicio Destino | Método | Autenticación |
|---|---|---|---|
| `/api/auth/**` | auth-service | POST | Pública |
| `/api/users/**` | auth-service | GET/POST/PUT | JWT (Admin) |
| `/api/config` | auth-service | GET/PUT | JWT (Admin) |
| `/api/products/**` | product-service | GET/POST/PUT/PATCH | JWT (lectura pública) |
| `/api/categories/**` | product-service | GET/POST/PUT | JWT (lectura pública) |
| `/api/suppliers/**` | product-service | GET/POST/PUT | JWT (lectura pública) |
| `/api/sales/**` | sale-service | GET/POST | JWT |
| `/api/dashboard` | sale-service | GET | JWT |
| `/api/attendance/**` | sale-service | GET/POST | JWT |
| `/api/scan/**` | sale-service | GET/POST | JWT |
| `/ws/**` | sale-service | WebSocket | JWT |

### 2.4 Comunicación Inter-Servicios (Feign Clients)

El **sale-service** se comunica con otros servicios mediante **OpenFeign**:

```java
// Product Client
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Map<String, Object> getProductById(@PathVariable String id);

    @PatchMapping("/api/products/{id}/stock")
    Map<String, Object> ajustarStock(@PathVariable String id, @RequestBody Map<String, Integer> body);
}

// Auth Client
@FeignClient(name = "auth-service")
public interface AuthClient {
    @GetMapping("/api/auth/user/id/{id}")
    Map<String, Object> getUserById(@PathVariable String id);
}
```

### 2.5 Circuit Breaker (Resilience4j)

| Parámetro | Valor |
|---|---|
| Sliding Window Size | 10 llamadas |
| Failure Rate Threshold | 50% |
| Wait Duration in Open State | 10 segundos |
| Permitted Calls in Half-Open State | 3 |
| Timeout Duration | 5 segundos |

## 3. Requisitos del Sistema

- **Java** 17 o superior (Eclipse Temurin recomendado)
- **Maven** 3.8 o superior
- **Docker** 24+ y **Docker Compose** 2.x (opcional, para ejecución contenerizada)
- **PostgreSQL** 16 (local o NeonDB cloud)
- **Git**

## 4. Instalación y Ejecución

### 4.1 Opción 1: Local con Docker Compose

```bash
# Clonar el repositorio
git clone https://github.com/TU_USUARIO/libreria-machy-v4.git
cd libreria-machy-v4

# Iniciar todos los servicios
docker compose up -d

# Ver logs
docker compose logs -f

# Detener servicios
docker compose down
```

Acceder a:
- Eureka Dashboard: http://localhost:8761
- Gateway (API): http://localhost:8080
- Frontend: http://localhost:8080 (servido por el gateway)

### 4.2 Opción 2: Local sin Docker (Maven)

```bash
# 1. Crear la base de datos y esquemas
psql -U postgres -c "CREATE DATABASE libreria_machy;"
psql -U postgres -d libreria_machy -f setup-schemas.sql

# 2. Iniciar servicios en orden
cd backend/discovery-service
mvn spring-boot:run                    # Puerto 8761

cd backend/auth-service
mvn spring-boot:run                    # Puerto 8081

cd backend/product-service
mvn spring-boot:run                    # Puerto 8082

cd backend/sale-service
mvn spring-boot:run                    # Puerto 8083

cd backend/gateway-service
mvn spring-boot:run                    # Puerto 8080
```

### 4.3 Opción 3: Railway (Cloud)

Sigue las instrucciones en [RAILWAY.md](../RAILWAY.md) para desplegar en Railway.

### 4.4 Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|---|---|---|
| `PORT` | Puerto del servidor | 8761 / 8080 / 8081 / 8082 / 8083 |
| `SPRING_DATASOURCE_URL` | URL de conexión a PostgreSQL | jdbc:postgresql://localhost:5432/libreria_machy?currentSchema=... |
| `SPRING_DATASOURCE_USERNAME` | Usuario de BD | postgres |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de BD | postgres |
| `DB_USERNAME` | Usuario de BD (auth-service) | postgres |
| `DB_PASSWORD` | Contraseña de BD (auth-service) | postgres |
| `EUREKA_URL` | URL del servidor Eureka | http://localhost:8761/eureka/ |
| `SERVICE_HOSTNAME` | Hostname del servicio para Eureka | localhost |
| `AUTH_SERVICE_URL` | URL del auth-service (gateway) | http://localhost:8081 |
| `PRODUCT_SERVICE_URL` | URL del product-service (gateway) | http://localhost:8082 |
| `SALE_SERVICE_URL` | URL del sale-service (gateway) | http://localhost:8083 |
| `CORS_ORIGINS` | Orígenes permitidos para CORS | * |
| `SMTP_HOST` | Servidor SMTP | smtp.gmail.com |
| `SMTP_PORT` | Puerto SMTP | 587 |
| `SMTP_USERNAME` | Usuario SMTP | — |
| `SMTP_PASSWORD` | Contraseña SMTP | — |
| `JWT_SECRET` | Secreto para firmar JWT | libreria_machy_jwt_secret_key_... |

## 5. Estructura del Proyecto

```
libreria-machy-v4/
├── backend/
│   ├── discovery-service/       # Eureka Server
│   │   ├── Dockerfile
│   │   └── src/
│   ├── gateway-service/         # API Gateway
│   │   ├── Dockerfile
│   │   └── src/
│   ├── auth-service/            # Autenticación y usuarios
│   │   ├── Dockerfile
│   │   └── src/
│   ├── product-service/         # Productos, categorías, proveedores
│   │   ├── Dockerfile
│   │   └── src/
│   └── sale-service/            # Ventas, asistencia, escaneo
│       ├── Dockerfile
│       └── src/
├── frontend/                    # Frontend (HTML, CSS, JS)
├── docs/
│   ├── MANUAL_TECNICO.md
│   └── MANUAL_USUARIO.md
├── setup-schemas.sql
├── setup-products.sql
├── setup-attendance.sql
├── docker-compose.yml
├── pom.xml                      # POM padre multi-módulo
└── README.md
```

## 6. Endpoints por Servicio

### discovery-service (8761)

| Endpoint | Descripción |
|---|---|
| `/` | Dashboard Eureka |
| `/eureka/apps` | Lista de servicios registrados (JSON) |
| `/actuator/health` | Health check |
| `/actuator/info` | Información del servicio |

### gateway-service (8080)

| Endpoint | Descripción |
|---|---|
| `/actuator/health` | Health check |
| `/actuator/gateway/routes` | Rutas configuradas |
| `/actuator/info` | Información del servicio |

### auth-service (8081)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/logout` | Cerrar sesión |
| POST | `/api/auth/recuperar` | Recuperar contraseña |
| GET | `/api/users` | Listar usuarios |
| POST | `/api/users` | Crear usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| GET | `/api/config` | Obtener configuración |
| PUT | `/api/config` | Actualizar configuración |
| GET | `/api/auth/user/id/{id}` | Obtener usuario por ID |
| GET | `/actuator/health` | Health check |

### product-service (8082)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/products` | Listar productos |
| GET | `/api/products/{id}` | Obtener producto por ID |
| GET | `/api/products/search?q=` | Buscar producto |
| POST | `/api/products` | Crear producto |
| PUT | `/api/products/{id}` | Actualizar producto |
| PATCH | `/api/products/{id}/toggle` | Cambiar estado activo/inactivo |
| PATCH | `/api/products/{id}/stock` | Ajustar stock |
| GET | `/api/categories` | Listar categorías |
| POST | `/api/categories` | Crear categoría |
| PUT | `/api/categories/{id}` | Actualizar categoría |
| GET | `/api/suppliers` | Listar proveedores |
| POST | `/api/suppliers` | Crear proveedor |
| PUT | `/api/suppliers/{id}` | Actualizar proveedor |
| GET | `/actuator/health` | Health check |

### sale-service (8083)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/sales` | Listar ventas |
| GET | `/api/sales/{id}` | Obtener venta |
| POST | `/api/sales` | Crear venta |
| POST | `/api/sales/{id}/cancel` | Cancelar/anular venta |
| GET | `/api/sales/reports` | Reporte de ventas |
| GET | `/api/dashboard` | Dashboard con indicadores |
| POST | `/api/attendance/check-in` | Marcar entrada |
| POST | `/api/attendance/check-out` | Marcar salida |
| GET | `/api/attendance/today` | Estado del día |
| GET | `/api/attendance/weekly-report` | Reporte semanal |
| POST | `/api/scan/session` | Crear sesión de escaneo |
| POST | `/api/scan/session/{pin}/verify` | Verificar PIN |
| GET | `/api/scan/session/{code}` | Obtener sesión por código |
| WS | `/ws` | WebSocket STOMP |

## 7. Seguridad

### 7.1 Flujo de Autenticación JWT

```
Cliente                    Gateway                 Auth Service
  │                          │                        │
  │── POST /api/auth/login ──►─────── /api/auth/login ──►│
  │                          │                        │
  │◄─────── JWT Token ──────◄────── JWT Token ───────◄│
  │                          │                        │
  │── GET /api/sales ───────►│                        │
  │    Authorization:       │── valida JWT ──────────►│
  │    Bearer <token>       │◄─────── OK ─────────────◄│
  │                          │                        │
  │◄─────── 200 OK ─────────◄│                        │
```

### 7.2 Roles de Usuario

| Rol | Permisos |
|---|---|
| `admin` | CRUD usuarios, CRUD productos, CRUD categorías, CRUD proveedores, configuración del sistema, todas las ventas, reportes |
| `vendedor` | Registrar ventas, ver productos, ver historial, marcar asistencia, escaneo remoto |

### 7.3 Características de Seguridad

- Passwords hasheados con **BCrypt**
- Tokens JWT con expiración de **8 horas**
- Endpoints públicos: login, recuperación, consulta de productos/categorías/proveedores
- Endpoints protegidos validan el token JWT en cada petición
- CORS configurado globalmente en el gateway
- Logout invalida el token (blacklist en memoria)
