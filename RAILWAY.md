# Deploy en Railway - Librería Machy v4.0

## Requisitos previos
- Cuenta en [railway.app](https://railway.app)
- Repositorio en GitHub con el proyecto (ya subido)
- NeonDB ya configurado en [neon.tech](https://neon.tech)
- Los schemas creados en NeonDB (ejecutar `setup-schemas.sql`)

## Variables de entorno compartidas (NeonDB)

Todas las conexiones a BD usan los mismos valores base. En Neon.tech obtienes:
- `DB_URL` = `jdbc:postgresql://ep-tu-proyecto.us-east-1.aws.neon.tech/neondb?sslmode=require`
- `DB_USERNAME` = usuario de Neon
- `DB_PASSWORD` = password de Neon

Cada servicio agrega `&currentSchema=mischema` automáticamente.

## Paso 1: Crear el proyecto en Railway

1. Ve a [railway.app](https://railway.app) → **New Project** → **Deploy from GitHub repo**
2. Selecciona tu repositorio (`jhnsys2059/libreria-machy-v4`)
3. Railway creará un servicio inicial. **Elimínalo** (no lo necesitamos)
4. Agrega los 5 servicios uno por uno usando **"+ New"** → **"GitHub Repo"**
   > **Nota:** El frontend está embebido en el gateway-service, no necesita servicio aparte.

**IMPORTANTE:** Para cada servicio, configura:
- **Root Directory**: `/`
- **Dockerfile Path**: la ruta al Dockerfile dentro del repo

### 1. Discovery Service (Eureka) — Puerto 8761
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile Path | `backend/discovery-service/Dockerfile` |

Sin variables de entorno extra (usa defaults).

### 2. Auth Service — Puerto 8081
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile Path | `backend/auth-service/Dockerfile` |

**Variables de entorno:**
```
PORT=8081
DB_URL=jdbc:postgresql://ep-tu-proyecto.up.railway.app/neondb?sslmode=require&currentSchema=auth_schema
DB_USERNAME=neondb_owner
DB_PASSWORD=tu_password_neon
JWT_SECRET=una_clave_segura_muy_larga_para_firmar_tokens
EUREKA_URL=https://discovery-service-tu-proyecto.up.railway.app/eureka/
```

### 3. Product Service — Puerto 8082
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile Path | `backend/product-service/Dockerfile` |

**Variables de entorno:**
```
PORT=8082
DB_URL=jdbc:postgresql://ep-tu-proyecto.up.railway.app/neondb?sslmode=require&currentSchema=product_schema
DB_USERNAME=neondb_owner
DB_PASSWORD=tu_password_neon
EUREKA_URL=https://discovery-service-tu-proyecto.up.railway.app/eureka/
```

### 4. Sale Service — Puerto 8083
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile Path | `backend/sale-service/Dockerfile` |

**Variables de entorno:**
```
PORT=8083
DB_URL=jdbc:postgresql://ep-tu-proyecto.up.railway.app/neondb?sslmode=require&currentSchema=sale_schema
DB_USERNAME=neondb_owner
DB_PASSWORD=tu_password_neon
AUTH_SERVICE_URL=https://auth-service-tu-proyecto.up.railway.app
PRODUCT_SERVICE_URL=https://product-service-tu-proyecto.up.railway.app
EUREKA_URL=https://discovery-service-tu-proyecto.up.railway.app/eureka/
```

### 5. Gateway Service — Puerto 8080 (PUERTA DE ENTRADA)
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile Path | `backend/gateway-service/Dockerfile` |

**Variables de entorno:**
```
PORT=8080
AUTH_SERVICE_URL=https://auth-service-tu-proyecto.up.railway.app
PRODUCT_SERVICE_URL=https://product-service-tu-proyecto.up.railway.app
SALE_SERVICE_URL=https://sale-service-tu-proyecto.up.railway.app
CORS_ORIGINS=*
EUREKA_URL=https://discovery-service-tu-proyecto.up.railway.app/eureka/
```

## Paso 2: Orden de deploy

Railway despliega en paralelo, pero los servicios dependen entre sí:

1. **Discovery Service** — espera a que esté "Active" (verde)
2. **Auth Service, Product Service, Sale Service** — en paralelo
3. **Gateway Service** — cuando los 3 anteriores estén activos (el frontend ya está embebido aquí)

Para controlar el orden, despliega solo Discovery primero, luego los otros, etc.

## Paso 3: Obtener las URLs de cada servicio

Una vez desplegado cada servicio, Railway asigna una URL tipo:
`https://<service-name>-xxxx.up.railway.app`

Copia esas URLs y úsalas en las variables de entorno de los servicios que las necesitan (Gateway, Sale Service). Railway redeploya automáticamente al cambiar variables.

## Paso 4: Verificar

1. **Eureka Dashboard**: `https://discovery-service-tu-proyecto.up.railway.app`
   - Deberías ver los servicios registrados
2. **Gateway (API)**: `https://gateway-service-tu-proyecto.up.railway.app`
3. **Login**: `admin` / `admin123`

## Notas importantes

- Los Dockerfiles usan **multi-stage build** (compilan el JAR, luego lo ejecutan)
- Cada build toma ~2-5 minutos en Railway free tier
- Railway free tier: 500 horas/mes, se pausa tras 30 min sin tráfico
- Si un servicio falla, revisa los logs en el dashboard de Railway
- No uses `localhost` en las URLs — siempre las URLs públicas de Railway
- El JWT_SECRET debe ser el mismo en todos los servicios que validen tokens (auth-service)
