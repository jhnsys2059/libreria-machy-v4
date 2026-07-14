# ANÁLISIS - Librería Machy v4.0

## 1. Actores del Sistema

| Actor | Descripción | Permisos |
|-------|-------------|----------|
| **Administrador** | Usuario con rol `admin`. Gestiona el sistema en su totalidad: usuarios, productos, categorías, proveedores, ventas, asistencia, reportes, backup y configuración. | Acceso completo a todos los módulos. Creado por defecto con credenciales `admin` / `admin123`. |
| **Vendedor/Cajero** | Usuario con rol `vendedor`. Realiza ventas, escanea códigos de barras y marca asistencia. | Solo puede registrar ventas (protegido por `X-User-Id`), cancelar ventas, marcar entrada/salida y ver su propio historial. No puede gestionar usuarios, productos, categorías ni proveedores. Creado por defecto: `ana` y `miguel`. |
| **Cliente** | Actor indirecto. Navega catálogo, realiza compras en mostrador atendido por vendedor. No interactúa directamente con el sistema. | Solo visualiza productos y recibe boletas. |

**Fuentes:** `DatabaseSeeder.java` (auth-service) crea 3 usuarios con roles `admin` y `vendedor`. `UserService.java` valida unicidad de username y correo, asigna rol por defecto `vendedor`. Los endpoints protegidos verifican headers `X-User-Id` y `X-User-Rol`.

---

## 2. Requerimientos Funcionales

| ID | Requerimiento | Endpoint(s) | Servicio |
|----|--------------|-------------|----------|
| RF-01 | Inicio de sesión (JWT) | `POST /api/auth/login` | Auth |
| RF-02 | Cierre de sesión (invalida token) | `POST /api/auth/logout` | Auth |
| RF-03 | Recuperación de contraseña por correo | `POST /api/auth/recover` | Auth |
| RF-04 | Listar usuarios (admin) | `GET /api/users` | Auth |
| RF-05 | Obtener usuario por ID | `GET /api/users/{id}` | Auth |
| RF-06 | Crear usuario (admin) | `POST /api/users` | Auth |
| RF-07 | Actualizar usuario (admin) | `PUT /api/users/{id}` | Auth |
| RF-08 | Activar/desactivar usuario | `PATCH /api/users/{id}/toggle` | Auth |
| RF-09 | Obtener usuario por username | `GET /api/auth/user/{username}` | Auth |
| RF-10 | Obtener usuario por ID (Feign) | `GET /api/auth/user/id/{id}` | Auth |
| RF-11 | Listar productos (con búsqueda y filtro) | `GET /api/products` | Product |
| RF-12 | Listar productos activos | `GET /api/products/active` | Product |
| RF-13 | Obtener producto por ID | `GET /api/products/{id}` | Product |
| RF-14 | Obtener producto por código de barras | `GET /api/products/by-code/{codigo}` | Product |
| RF-15 | Crear producto | `POST /api/products` | Product |
| RF-16 | Actualizar producto | `PUT /api/products/{id}` | Product |
| RF-17 | Activar/descontinuar producto | `PATCH /api/products/{id}/toggle` | Product |
| RF-18 | Ajustar stock de producto | `PUT /api/products/{id}/stock` | Product |
| RF-19 | Resumen de inventario | `GET /api/products/inventory-summary` | Product |
| RF-20 | Listar categorías | `GET /api/categories` | Product |
| RF-21 | Listar categorías activas | `GET /api/categories/active` | Product |
| RF-22 | Obtener categoría por ID | `GET /api/categories/{id}` | Product |
| RF-23 | Crear categoría | `POST /api/categories` | Product |
| RF-24 | Actualizar categoría | `PUT /api/categories/{id}` | Product |
| RF-25 | Activar/desactivar categoría | `PATCH /api/categories/{id}/toggle` | Product |
| RF-26 | Listar proveedores | `GET /api/suppliers` | Product |
| RF-27 | Listar proveedores activos | `GET /api/suppliers/active` | Product |
| RF-28 | Obtener proveedor por ID | `GET /api/suppliers/{id}` | Product |
| RF-29 | Crear proveedor | `POST /api/suppliers` | Product |
| RF-30 | Actualizar proveedor | `PUT /api/suppliers/{id}` | Product |
| RF-31 | Activar/desactivar proveedor | `PATCH /api/suppliers/{id}/toggle` | Product |
| RF-32 | Exportar datos (backup) - Auth | `GET /api/auth/backup/export` | Auth |
| RF-33 | Importar datos (restore) - Auth | `POST /api/auth/backup/import` | Auth |
| RF-34 | Exportar datos (backup) - Product | `GET /api/products/backup/export` | Product |
| RF-35 | Importar datos (restore) - Product | `POST /api/products/backup/import` | Product |
| RF-36 | Exportar datos (backup) - Sale | `GET /api/sales/backup/export` | Sale |
| RF-37 | Importar datos (restore) - Sale | `POST /api/sales/backup/import` | Sale |
| RF-38 | Obtener configuración del sistema | `GET /api/config` | Auth |
| RF-39 | Guardar configuración del sistema | `PUT /api/config` | Auth |
| RF-40 | Listar ventas (admin todas, vendedor sus ventas) | `GET /api/sales` | Sale |
| RF-41 | Obtener venta por ID | `GET /api/sales/{id}` | Sale |
| RF-42 | Registrar venta (con descuento, cálculo IGV, boleta) | `POST /api/sales` | Sale |
| RF-43 | Cancelar venta (restaura stock) | `POST /api/sales/{id}/cancel` | Sale |
| RF-44 | Reporte de ventas (top productos, ventas por día) | `GET /api/sales/reports` | Sale |
| RF-45 | Dashboard (ventas hoy, totales, ingresos) | `GET /api/dashboard` | Sale |
| RF-46 | Estado de asistencia hoy | `GET /api/attendance/status` | Sale |
| RF-47 | Marcar entrada (check-in) | `POST /api/attendance/check-in` | Sale |
| RF-48 | Marcar salida (check-out) | `POST /api/attendance/check-out` | Sale |
| RF-49 | Reporte semanal de asistencia | `GET /api/attendance/weekly-report` | Sale |
| RF-50 | Log detallado de asistencia | `GET /api/attendance/log` | Sale |
| RF-51 | Marcar asistencia manual (admin) | `POST /api/attendance/admin` | Sale |
| RF-52 | Crear sesión de escaneo remoto (genera PIN y QR) | `POST /api/scan/session` | Sale |
| RF-53 | Verificar PIN de sesión | `POST /api/scan/session/{sessionId}/verify-pin` | Sale |
| RF-54 | Enviar código escaneado | `POST /api/scan/session/{sessionId}/code` | Sale |
| RF-55 | Finalizar sesión de escaneo | `DELETE /api/scan/session/{sessionId}` | Sale |
| RF-56 | WebSocket - Unirse a sesión (enviar PIN) | `/app/scan.join.{sessionId}` | Sale |
| RF-57 | WebSocket - Enviar código escaneado | `/app/scan.code.{sessionId}` | Sale |

**Fuentes:** Controller classes de `auth-service`, `product-service` y `sale-service`. Gateway routes en `gateway-service/application.yml`.

---

## 3. Requerimientos No Funcionales

### Seguridad
- **Autenticación JWT:** Tokens generados con `HMAC-SHA256` (jjwt 0.12.6), expiran en 8 horas (28800000 ms). Claims: `userId`, `username`, `rol`.
- **Token Blacklist:** Los tokens invalidados al cerrar sesión se almacenan en `LinkedHashMap` con expiración automática (purge cada 10 min). Máximo 1000 tokens.
- **Contraseñas:** Encriptadas con `BCryptPasswordEncoder` (strength 12).
- **Control de acceso basado en roles:** Los endpoints verifican headers `X-User-Id` y `X-User-Rol` para filtrar datos. Los endpoints de solo admin (POST users, POST products) están protegidos.
- **Protección contra fuerza bruta:** 5 intentos fallidos bloquean la cuenta por 15 minutos.

### Escalabilidad
- **Microservicios con Eureka Discovery:** 5 servicios (`discovery`, `gateway`, `auth`, `product`, `sale`) se registran automáticamente en Eureka Server (puerto 8761).
- **Balanceo de carga:** Spring Cloud Gateway con `discovery locator` enabled, enrutamiento basado en `service-id`.
- **Gateway centralizado:** Puerto 8080, enruta peticiones a los servicios correspondientes con timeout de 30s.

### Disponibilidad
- **Despliegue en Railway:** 6 servicios Dockerizados con multi-stage build.
- **Health Checks:** Actuator endpoints `/actuator/health` e `/actuator/info` expuestos en todos los servicios.
- **Circuit Breaker Resilience4j:** Protege contra fallos en cascada con sliding window de 10 llamadas, umbral de fallo 50%, tiempo en estado abierto 10s.

### Portabilidad
- **Java 17** con **Spring Boot 3.4.4** y **Spring Cloud 2024.0.1**.
- **Docker:** Cada servicio tiene su propio `Dockerfile` con multi-stage build.
- **PostgreSQL en NeonDB:** Base de datos cloud, esquemas separados (`auth_schema`, `product_schema`, `sale_schema`).
- **Configuración externalizada:** Variables de entorno para puertos, URLs, credenciales y JWT secret.

### Mantenibilidad
- **5 servicios independientes:** Cada uno con su propia API REST, base de datos y responsabilidad.
- **DTOs separados:** `LoginRequest`, `LoginResponse`, `UserRequest`, `ProductRequest`, `SaleRequest`, etc.
- **Comunicación Feign Clients:** Sale service consume auth-service y product-service via OpenFeign.
- **Logging:** Sistema de logs persistente (`LogEntry`) con niveles, módulos y trazabilidad.
- **Documentación Swagger:** Cada servicio expone `/swagger-ui.html` y `/api-docs`.

### Rapidez
- **HikariCP Connection Pool:** Configurado con máximo 5 conexiones, mínimo 1, timeout 30s.
- **Feign Clients síncronos:** Comunicación directa entre servicios.
- **Circuit Breaker:** Timeouts de 5s para llamadas entre servicios.
- **Jackson optimizado:** `default-property-inclusion: non_null` para respuestas ligeras.
- **Zona horaria Perú:** `America/Lima` configurada en todos los servicios.

**Fuentes:** `application.yml` de cada servicio, `pom.xml` (parent: Spring Boot 3.4.4, Spring Cloud 2024.0.1, Java 17), `ARCHITECTURE.md`, `RAILWAY.md`, `SecurityConfig.java`, `JwtUtil.java`, `TokenBlacklistService.java`.

---

## 4. Historias de Usuario

| ID | Historia |
|----|----------|
| HU-01 | Como **administrador**, quiero **iniciar sesión en el sistema** para **acceder a las funcionalidades de gestión**. |
| HU-02 | Como **administrador**, quiero **gestionar usuarios (crear, editar, activar/desactivar)** para **controlar quién accede al sistema**. |
| HU-03 | Como **administrador**, quiero **registrar y actualizar productos con código de barras** para **mantener el inventario actualizado**. |
| HU-04 | Como **administrador**, quiero **gestionar categorías y proveedores** para **organizar el catálogo de productos**. |
| HU-05 | Como **vendedor**, quiero **registrar ventas con cálculo automático de IGV y descuento** para **agilizar la atención al cliente**. |
| HU-06 | Como **vendedor**, quiero **escanear códigos de barras con mi celular** para **agregar productos rápidamente a la venta**. |
| HU-07 | Como **vendedor**, quiero **marcar mi entrada y salida** para **registrar mi asistencia diaria**. |
| HU-08 | Como **administrador**, quiero **ver el reporte semanal de asistencia** para **supervisar el cumplimiento del personal**. |
| HU-09 | Como **administrador**, quiero **ver el dashboard con ventas del día y totales** para **tomar decisiones comerciales**. |
| HU-10 | Como **administrador**, quiero **generar reportes de ventas con top productos** para **analizar el rendimiento del negocio**. |
| HU-11 | Como **administrador**, quiero **exportar e importar los datos (backup/restore)** para **prevenir pérdidas de información**. |
| HU-12 | Como **administrador**, quiero **configurar parámetros del sistema** para **personalizar el comportamiento de la aplicación**. |
| HU-13 | Como **administrador**, quiero **recuperar mi contraseña por correo electrónico** para **no perder el acceso al sistema**. |
| HU-14 | Como **vendedor**, quiero **cancelar una venta** para **corregir errores en las transacciones**. |

---

## 5. Casos de Uso

| Código | Caso de Uso | Actor | Descripción |
|--------|-------------|-------|-------------|
| CU-01 | Iniciar Sesión | Admin, Vendedor | El usuario ingresa username y contraseña; el sistema valida credenciales, verifica estado activo y bloqueo, y retorna un token JWT. |
| CU-02 | Gestionar Usuarios | Admin | CRUD de usuarios: crear, listar, obtener, actualizar datos y activar/desactivar cuentas. Validación de unicidad de username y correo. |
| CU-03 | Gestionar Productos | Admin | CRUD de productos con código de barras único, precio compra/venta, stock, stock mínimo, categoría y proveedor. Incluye búsqueda por nombre/código y filtro por categoría. |
| CU-04 | Gestionar Categorías | Admin | CRUD de categorías para clasificar productos. Cada categoría tiene nombre, descripción y estado activo. |
| CU-05 | Gestionar Proveedores | Admin | CRUD de proveedores con RUC, contacto, teléfono, email y dirección. |
| CU-06 | Registrar Venta | Vendedor | Registra una venta con productos, cantidades, descuento y pago. El sistema calcula subtotal, IGV (18%), total, vuelto, actualiza stock vía Feign y genera boleta automática si el total >= S/5.00. |
| CU-07 | Cancelar Venta | Admin, Vendedor | Anula una venta confirmada registrando motivo. El sistema restaura el stock de cada producto vendido. Solo se puede cancelar ventas en estado "confirmada". |
| CU-08 | Escanear Código de Barras | Vendedor | El vendedor crea una sesión de escaneo; el sistema genera un PIN de 4 dígitos y un código QR. El vendedor escanea desde su celular, ingresa el PIN para autenticarse vía WebSocket y envía códigos de barras que se reciben en tiempo real en el POS. |
| CU-09 | Controlar Asistencia | Vendedor, Admin | El vendedor marca entrada/salida; el sistema calcula tardanza (con 15 min de tolerancia), horas trabajadas y verifica cumplimiento de turno (mínimo 5h). El admin puede marcar asistencia manual para cualquier usuario. |
| CU-10 | Generar Reportes | Admin | Reporte de ventas con ingresos totales, ticket promedio, boletas emitidas, top 8 productos más vendidos y ventas por día de la última semana. |
| CU-11 | Realizar Backup | Admin | Exporta todos los datos del servicio (usuarios, logs, productos, categorías, proveedores, ventas, asistencia) en formato JSON. Importa datos previamente exportados para restauración. |
| CU-12 | Configurar Sistema | Admin | Visualiza y modifica parámetros de configuración del sistema almacenados en la tabla `config` (clave-valor). |
| CU-13 | Ver Dashboard | Admin, Vendedor | Muestra resumen del día: cantidad de ventas hoy, ventas totales confirmadas e ingresos totales acumulados. |
| CU-14 | Recuperar Contraseña | Admin, Vendedor | El usuario solicita recuperación; el sistema genera una contraseña temporal, la guarda encriptada y la envía al correo registrado del usuario. |

---

## 6. Reglas del Negocio

| ID | Regla | Fundamento (Código) |
|----|-------|---------------------|
| RN-01 | **Stock suficiente:** No se puede registrar una venta si el stock de algún producto es menor a la cantidad solicitada. | `SaleService.java:90-92` — `if (stockActual < itemReq.getCantidad()) throw new RuntimeException(...)` |
| RN-02 | **Usuarios únicos:** No se permiten nombres de usuario ni correos duplicados. | `UserService.java:39-43` — `existsByUsername`, `existsByCorreo` |
| RN-03 | **Roles y permisos:** El administrador puede gestionar usuarios, productos, categorías y proveedores. El vendedor solo puede registrar ventas y marcar asistencia. | `UserService.java:54` — rol por defecto "vendedor". Los endpoints `POST/PUT /api/users` y `POST/PUT /api/products` son solo admin. |
| RN-04 | **Venta cancelada restaura stock:** Al anular una venta, el stock de cada producto se incrementa en la cantidad vendida. | `SaleService.java:164-167` — `adjustStock(productId, +cantidad)` |
| RN-05 | **Sin doble entrada:** No se puede registrar una entrada de asistencia si ya existe un registro para el mismo usuario el día de hoy. | `AttendanceService.java:64-66` — `if (findByUsuarioIdAndFecha(usuarioId, hoy).isPresent()) throw ...` |
| RN-06 | **Salida requiere entrada:** No se puede marcar salida sin haber marcado entrada previamente el mismo día. | `AttendanceService.java:94-99` — `orElseThrow(() -> new RuntimeException("No hay registro de entrada hoy"))` |
| RN-07 | **Sin doble salida:** No se puede marcar salida si ya se registró salida hoy. | `AttendanceService.java:98-100` — `if (reg.getHoraSalida() != null) throw ...` |
| RN-08 | **Tolerancia de tardanza:** La tardanza se calcula con 15 minutos de tolerancia sobre la hora de inicio del turno. | `AttendanceService.java:177-186` — `return Math.max(0, (int) (mins - inicioMins - 15))` |
| RN-09 | **Turnos y horarios:** Turno "mañana" inicia 8:00, "tarde" 14:00, "completo" 8:00. Se considera cumplimiento si se trabajan al menos 5 horas. | `AttendanceService.java:111, 177-186` |
| RN-10 | **Sesión de escaneo con PIN:** Las sesiones de escaneo remoto requieren autenticación mediante PIN de 4 dígitos. Las sesiones no autenticadas no pueden enviar códigos. | `ScanService.java` — `sessionPins`, `authenticatedSessions`, `verifyPin()` |
| RN-11 | **Expiración de JWT:** Los tokens JWT expiran después de 8 horas (28800000 ms). | `JwtUtil.java:32`, `application.yml:56` |
| RN-12 | **Bloqueo por intentos:** Después de 5 intentos fallidos de inicio de sesión, la cuenta se bloquea por 15 minutos. | `AuthService.java:60-63` — `if (intentosFallidos >= 5) bloqueadoHasta = now + 15 min` |
| RN-13 | **Boleta automática:** Se genera boleta automáticamente para ventas con total >= S/5.00. | `SaleService.java:120` — `boolean emiteBoleta = total.compareTo(new BigDecimal("5.00")) >= 0` |
| RN-14 | **Código de barras único:** No se puede registrar un producto con un código de barras que ya exista. | `ProductService.java:54-56` — `if (existsByCodigo()) throw ...` |

---

## 7. Priorización

### Prioridad ALTA
| ID | Requerimiento | Justificación |
|----|--------------|---------------|
| RF-01 | Inicio de sesión | Sin autenticación no se puede acceder al sistema. |
| RF-02 | Cierre de sesión | Seguridad y control de acceso. |
| RF-04 al RF-08 | Gestión de usuarios | Base para el control de acceso y administración del personal. |
| RF-11 al RF-18 | Gestión de productos | Núcleo del negocio: sin productos no hay ventas. |
| RF-23, RF-24 | Gestión de categorías | Organización del catálogo. |
| RF-29, RF-30 | Gestión de proveedores | Abastecimiento de productos. |
| RF-42 | Registrar venta | Función principal del sistema de punto de venta. |

### Prioridad MEDIA
| ID | Requerimiento | Justificación |
|----|--------------|---------------|
| RF-40, RF-41 | Listar y ver ventas | Consulta de transacciones, soporte para cancelaciones. |
| RF-43 | Cancelar venta | Corrección de errores en ventas. |
| RF-44 | Reporte de ventas | Análisis de rendimiento del negocio. |
| RF-45 | Dashboard | Visibilidad rápida del estado del negocio. |
| RF-46 al RF-51 | Control de asistencia | Gestión de personal y horarios. |
| RF-09, RF-10 | Consulta de usuarios | Necesario para comunicación Feign entre servicios. |

### Prioridad BAJA
| ID | Requerimiento | Justificación |
|----|--------------|---------------|
| RF-32 al RF-37 | Backup/Restore | Importante para contingencia pero no crítico para operación diaria. |
| RF-38, RF-39 | Configuración del sistema | Personalización, no afecta funcionalidad base. |
| RF-52 al RF-57 | Escaneo remoto | Funcionalidad adicional que mejora la experiencia pero no es indispensable. |
| RF-14 | Búsqueda por código de barras | Complementaria a la gestión de productos. |
