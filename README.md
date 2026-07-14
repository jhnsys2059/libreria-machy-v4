# Librería Machy v4.0 — Microservices

Sistema de gestión comercial con arquitectura de microservicios para la administración de una librería. Incluye control de inventario, ventas, asistencia del personal y escaneo remoto de productos.

## Arquitectura

```
Gateway (8080) → Auth (8081) | Product (8082) | Sale (8083)
                        ↘ Discovery (8761) ↙
                                  ↓
                          PostgreSQL 16
```

- **discovery-service** — Eureka Server (registro y descubrimiento)
- **gateway-service** — Spring Cloud Gateway (enrutamiento, CORS, balanceo)
- **auth-service** — Autenticación JWT, usuarios, configuración
- **product-service** — Productos, categorías, proveedores, stock
- **sale-service** — Ventas, asistencia, escaneo remoto, dashboard

## Tech Stack

Java 17, Spring Boot 3.4.4, Spring Cloud 2024.0.1, PostgreSQL 16, Eureka, Feign, Resilience4j, JWT, WebSocket STOMP, Docker, Railway.

## Quick Start

```bash
# Clonar e iniciar con Docker Compose
git clone https://github.com/TU_USUARIO/libreria-machy-v4.git
cd libreria-machy-v4
docker compose up -d
```

Servicios disponibles en:
- **Gateway (API + Frontend):** http://localhost:8080
- **Eureka Dashboard:** http://localhost:8761

## Credenciales por Defecto

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `ana` | `123456` | Vendedor |
| `miguel` | `123456` | Vendedor |

## Documentación

- [Manual Técnico](docs/MANUAL_TECNICO.md) — Arquitectura, instalación, endpoints, seguridad
- [Manual de Usuario](docs/MANUAL_USUARIO.md) — Guía de uso del sistema
- [Railway Deploy Guide](RAILWAY.md) — Despliegue en Railway
- [Architecture Overview](ARCHITECTURE.md) — Diagrama y descripción de la arquitectura

## Deployment

El proyecto está desplegado en Railway. Para desplegar tu propia instancia, sigue [RAILWAY.md](RAILWAY.md).
