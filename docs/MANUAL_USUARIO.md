# Manual de Usuario — Librería Machy v4.0

## 1. Introducción

**Librería Machy v4.0** es un sistema de gestión comercial diseñado para la administración de una librería. Permite gestionar productos, ventas, usuarios, asistencia del personal y escaneo remoto de productos.

### Cómo acceder

- **Producción (Railway):** [https://gateway-service-tu-proyecto.up.railway.app](https://gateway-service-tu-proyecto.up.railway.app)
- **Local (Docker):** http://localhost:8080
- **Local (Maven):** http://localhost:8080

---

## 2. Inicio de Sesión

### Credenciales por Defecto

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `ana` | `123456` | Vendedor |
| `miguel` | `123456` | Vendedor |

### Pantalla de Login

[CAPTURA: Pantalla de login]

1. Ingresa tu **nombre de usuario** en el campo correspondiente.
2. Ingresa tu **contraseña**.
3. Haz clic en **"Iniciar Sesión"**.

### Recuperación de Contraseña

1. En la pantalla de login, haz clic en **"¿Olvidaste tu contraseña?"**.
2. Ingresa tu **nombre de usuario** y tu **correo electrónico** registrado.
3. Recibirás un enlace de recuperación en tu correo.
4. Sigue el enlace para crear una nueva contraseña.

---

## 3. Dashboard

Al iniciar sesión, verás el panel principal con los indicadores del día:

[CAPTURA: Dashboard principal]

- **Ventas del día:** Total de ventas realizadas hoy.
- **Ingresos del día:** Suma total de ingresos generados hoy.
- **Productos vendidos:** Cantidad de unidades vendidas hoy.
- **Asistencia:** Personal que ha marcado entrada hoy.

---

## 4. Gestión de Productos

### 4.1 Listar Productos

[CAPTURA: Pantalla de lista de productos]

- Navega a **Productos → Listar Productos**.
- La tabla muestra: código, nombre, categoría, precio, stock y estado.
- Usa el campo de búsqueda para filtrar por **código** o **nombre**.

### 4.2 Crear Producto

1. Haz clic en **"Nuevo Producto"**.
2. Completa los campos:
   - **Código:** Código único del producto.
   - **Nombre:** Nombre descriptivo.
   - **Descripción:** Detalles del producto.
   - **Categoría:** Selecciona una categoría existente.
   - **Proveedor:** Selecciona un proveedor.
   - **Precio de compra:** Costo del producto.
   - **Precio de venta:** Precio al público.
   - **Stock inicial:** Cantidad en inventario.
   - **Stock mínimo:** Nivel mínimo para alerta.
3. Haz clic en **"Guardar"**.

### 4.3 Editar Producto

1. Busca el producto en la lista.
2. Haz clic en el icono **✏️ (Editar)**.
3. Modifica los campos necesarios.
4. Haz clic en **"Guardar cambios"**.

### 4.4 Eliminar / Desactivar Producto

- Los productos no se eliminan físicamente; se **desactivan** cambiando su estado.
- Busca el producto y haz clic en el botón de **estado** (activo/inactivo).

### 4.5 Control de Stock

- El stock se actualiza automáticamente al registrar una venta.
- Puedes **ajustar manualmente** el stock desde la ficha del producto.
- Los productos con stock por debajo del mínimo se marcan con una alerta **🟡 (stock bajo)**.

---

## 5. Gestión de Ventas

### 5.1 Registrar Venta

[CAPTURA: Pantalla de nueva venta]

1. Navega a **Ventas → Nueva Venta**.
2. **Agrega productos** de una de las siguientes formas:
   - **Escanear código:** Usa un lector de código de barras.
   - **Buscar:** Escribe el nombre o código del producto.
   - **Seleccionar:** Elige de la lista de productos.
3. Para cada producto, indica la **cantidad**.
4. El sistema calcula automáticamente el **total**.
5. Selecciona el **método de pago** (efectivo, tarjeta, yape, plin).
6. Si es en efectivo, ingresa el **monto con el que paga** el cliente para calcular el vuelto.
7. Haz clic en **"Confirmar Venta"**.

### 5.2 Ver Historial de Ventas

[CAPTURA: Pantalla de historial de ventas]

1. Navega a **Ventas → Historial**.
2. Puedes **filtrar por fecha** usando el selector de rango.
3. La tabla muestra: comprobante, cliente, total, fecha, estado y usuario que atendió.
4. Haz clic en una venta para ver el **detalle** (productos, cantidades, subtotales).

### 5.3 Cancelar Venta

1. Busca la venta en el historial.
2. Haz clic en el botón **"Cancelar"** (solo si es del día actual).
3. Confirma la cancelación.
4. El stock de los productos se **restaura automáticamente**.

---

## 6. Escaneo Remoto

### 6.1 Crear Sesión de Escaneo

[CAPTURA: Pantalla de escaneo remoto]

1. Navega a **Escaneo Remoto**.
2. Haz clic en **"Nueva Sesión"**.
3. El sistema genera un **código QR** y un **código numérico**.

### 6.2 Escanear con Celular

1. En tu celular, abre la cámara y escanea el **código QR** (o ingresa manualmente la URL).
2. Se abrirá la interfaz de escaneo móvil.
3. Apunta la cámara al **código de barras** del producto.
4. El producto se agrega automáticamente a la sesión de venta.

### 6.3 Verificar PIN

- Cada sesión de escaneo tiene un **PIN** de verificación.
- El PIN se muestra en la pantalla principal de la sesión.
- El dispositivo móvil debe verificar el PIN para conectarse a la sesión correcta.

---

## 7. Asistencia

### 7.1 Marcar Entrada

[CAPTURA: Pantalla de asistencia]

1. Navega a **Asistencia → Marcar Entrada**.
2. Confirma tu **usuario** y la **hora actual**.
3. Haz clic en **"Check-In"**.

### 7.2 Marcar Salida

1. Al finalizar tu turno, navega a **Asistencia → Marcar Salida**.
2. Haz clic en **"Check-Out"**.
3. El sistema registra la hora de salida y calcula las horas trabajadas.

### 7.3 Ver Estado del Día

- Muestra quién ha marcado entrada y quién falta por marcar.
- Horarios de entrada y salida registrados.

### 7.4 Reporte Semanal

[CAPTURA: Reporte semanal de asistencia]

1. Navega a **Asistencia → Reporte Semanal**.
2. Selecciona la **semana** que deseas consultar.
3. La tabla muestra por cada día: entrada, salida, horas trabajadas y incidencias.

---

## 8. Administración

### 8.1 Gestión de Usuarios

[CAPTURA: Pantalla de usuarios]

1. Navega a **Administración → Usuarios**.
2. Puedes **crear**, **editar** o **desactivar** usuarios.
3. Cada usuario tiene: nombre de usuario, nombre completo, rol (admin/vendedor), correo y contraseña.

### 8.2 Gestión de Categorías

1. Navega a **Administración → Categorías**.
2. **Crear:** Ingresa nombre y descripción.
3. **Editar:** Modifica los datos de la categoría.
4. Las categorías se usan para organizar los productos.

### 8.3 Gestión de Proveedores

1. Navega a **Administración → Proveedores**.
2. **Crear:** Ingresa nombre, RUC, teléfono, correo y dirección.
3. **Editar:** Modifica los datos del proveedor.

### 8.4 Configuración del Sistema

[CAPTURA: Pantalla de configuración]

1. Navega a **Administración → Configuración**.
2. Parámetros configurables:
   - **Nombre del negocio** (aparece en comprobantes).
   - **Dirección** del negocio.
   - **Teléfono** de contacto.
   - **RUC** de la empresa.
   - **IGV** (porcentaje de impuesto).
   - **Mensaje personalizado** en comprobantes.

### 8.5 Backup de Datos

1. Navega a **Administración → Backup**.
2. Haz clic en **"Generar Backup"** para crear una copia de seguridad.
3. Los backups incluyen: productos, ventas, usuarios y configuración.
4. Los archivos se descargan en formato JSON.
