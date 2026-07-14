# Deploy en Railway - Librería Machy v4.0

## Requisitos previos
- Cuenta en [railway.app](https://railway.app)
- Repositorio en GitHub con el proyecto
- NeonDB ya configurado (neon.com)
- Las tablas ya creadas en NeonDB (`setup-schemas.sql`, `setup-products.sql`, `setup-attendance.sql`)

## Paso 1: Subir el proyecto a GitHub

```bash
cd Libreria_Machy_v4.0-microservices
git init
git add .
git commit -m "v4.0 - Microservices + Railway config"
git remote add origin https://github.com/TU_USUARIO/libreria-machy-v4.git
git push -u origin main
```

## Paso 2: Crear el proyecto en Railway

1. Ve a [railway.app](https://railway.app) → **New Project** → **Deploy from GitHub repo**
2. Selecciona tu repositorio
3. Railway creará un servicio inicial. **Elimínalo** (no lo necesitamos)
4. Ahora agrega los 6 servicios uno por uno usando **"+ New"** → **"GitHub Repo"**

**IMPORTANTE:** Para cada servicio, configura:
- **Root Directory**: `/` (dejar vacío o `/`) — porque los Dockerfiles copian desde la raíz
- **Dockerfile**: la ruta al Dockerfile dentro del repo

### 2.1 Discovery Service (Eureka Server)
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile | `backend/discovery-service/Dockerfile` |
| Puerto | 8761 |

**Variables de entorno:**
```
PORT=8761
```

### 2.2 Gateway Service
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile | `backend/gateway-service/Dockerfile` |
| Puerto | 8080 |

**Variables de entorno:**
```
PORT=8080
AUTH_SERVICE_URL=http://auth-service-TU-PROYECTO.up.railway.app
PRODUCT_SERVICE_URL=http://product-service-TU-PROYECTO.up.railway.app
SALE_SERVICE_URL=http://sale-service-TU-PROYECTO.up.railway.app
FRONTEND_SERVICE_URL=http://frontend-service-TU-PROYECTO.up.railway.app
CORS_ORIGINS=*
```

### 2.3 Auth Service
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile | `backend/auth-service/Dockerfile` |
| Puerto | 8081 |

**Variables de entorno:**
```
PORT=8081
```

### 2.4 Product Service
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile | `backend/product-service/Dockerfile` |
| Puerto | 8082 |

**Variables de entorno:**
```
PORT=8082
```

### 2.5 Sale Service
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile | `backend/sale-service/Dockerfile` |
| Puerto | 8083 |

**Variables de entorno:**
```
PORT=8083
```

### 2.6 Frontend Service
| Campo | Valor |
|-------|-------|
| Root Directory | `/` |
| Dockerfile | `frontend/frontend-service/Dockerfile` |
| Puerto | 8084 |

**Variables de entorno:**
```
PORT=8084
```

## Paso 3: Orden de deploy

Railway despliega en paralelo, pero los servicios dependen entre sí. El orden recomendado es:

1. **Discovery Service** (espera a que esté "Active")
2. **Auth Service, Product Service, Sale Service** (en paralelo)
3. **Gateway Service** (cuando los 3 anteriores estén activos)
4. **Frontend Service** (al final)

## Paso 4: Configurar URLs internas del Gateway

Una vez que todos los servicios tengan URL (aparecen en cada servicio del dashboard), **edita las variables del Gateway**:

```
AUTH_SERVICE_URL=https://auth-service-TU-PROYECTO.up.railway.app
PRODUCT_SERVICE_URL=https://product-service-TU-PROYECTO.up.railway.app
SALE_SERVICE_URL=https://sale-service-TU-PROYECTO.up.railway.app
FRONTEND_SERVICE_URL=https://frontend-service-TU-PROYECTO.up.railway.app
CORS_ORIGINS=*
```

Railway redeploya automáticamente al cambiar variables.

## Paso 5: Verificar

1. Abre el **Eureka Dashboard**: `https://discovery-service-TU-PROYECTO.up.railway.app`
   - Deberías ver los 5 servicios registrados (auth, product, sale, gateway, frontend)
2. Abre el **Frontend**: `https://gateway-TU-PROYECTO.up.railway.app`
3. Login: `admin` / `admin123`

## Notas importantes

- Los Dockerfiles usan **multi-stage build** (compilan el JAR primero, luego lo ejecutan)
- Cada build toma ~2-5 minutos en Railway free tier
- Si un servicio falla, revisa los logs en el dashboard de Railway
- Railway free tier: 500 horas/mes, se pausa tras 30 min sin tráfico
- La URL del gateway es la URL **pública** que usarás para acceder al sistema
- NeonDB ya está en la nube, no necesita deploy adicional
