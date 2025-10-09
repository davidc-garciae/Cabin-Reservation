# 📚 Documentación de la API - Cabin Reservation System

Este directorio contiene la documentación completa de la API del sistema de reservas de cabañas.

## 📁 Archivos Disponibles

### 🔧 `openapi.yaml`

**Documentación OpenAPI 3.0 completa** de todos los endpoints de la API.

**Incluye:**

- ✅ **52 endpoints** documentados completamente
- ✅ **Nuevas funcionalidades v2.0** (registro público, horarios)
- ✅ **Esquemas de datos** detallados con ejemplos
- ✅ **Códigos de respuesta** y mensajes de error
- ✅ **Autenticación JWT** documentada
- ✅ **Validaciones** y restricciones

### 🌐 **Acceso a la Documentación**

#### Opción 1: Swagger UI (Recomendado)

```
http://localhost:8080/swagger-ui.html
```

- Interfaz interactiva
- Pruebas directas desde el navegador
- Documentación siempre actualizada

#### Opción 2: Archivo OpenAPI

```
docs/openapi.yaml
```

- Documentación en formato YAML
- Compatible con herramientas de terceros
- Versionado y controlado

#### Opción 3: Generar documentación estática

```bash
# Usando swagger-codegen
swagger-codegen generate -i docs/openapi.yaml -l html2 -o docs/html

# Usando redoc-cli
redoc-cli build docs/openapi.yaml --output docs/index.html
```

## 🆕 **Nuevas Funcionalidades v2.0**

### 🔐 **Registro Público de Usuarios**

- **Endpoint:** `POST /api/auth/register`
- **Descripción:** Permite a nuevos usuarios registrarse con documento válido
- **Validaciones:** Documento activo, email único, PIN de 4 dígitos
- **Rol por defecto:** PROFESSOR

### ⏰ **Horarios de Check-in/Check-out**

- **Campos nuevos:** `checkInTime`, `checkOutTime` en reservas
- **Campos nuevos:** `defaultCheckInTime`, `defaultCheckOutTime` en cabañas
- **Formato:** HH:mm (24 horas)
- **Valores por defecto:** 15:00 (check-in), 11:00 (check-out)

## 📊 **Endpoints Principales**

### 🔐 **Autenticación (6 endpoints)**

- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - **NUEVO** Registro público
- `POST /api/auth/refresh` - Renovar token
- `POST /api/auth/recover-password` - Recuperar contraseña
- `POST /api/auth/reset-password` - Restablecer contraseña
- `POST /api/auth/validate-token` - Validar token

### 🏠 **Cabañas Públicas (3 endpoints)**

- `GET /api/cabins` - Listar cabañas activas
- `GET /api/cabins/{id}` - Obtener cabaña por ID
- `GET /api/cabins/search` - Buscar cabañas

### 📅 **Disponibilidad Pública (6 endpoints)**

- `GET /api/availability` - Fechas disponibles
- `GET /api/availability/calendar` - Calendario de disponibilidad
- `GET /api/availability/calendar/list` - Calendario como lista
- `GET /api/availability/cabin/{id}` - Disponibilidad por cabaña
- `GET /api/availability/cabin/{id}/calendar` - Calendario por cabaña
- `GET /api/availability/cabin/{id}/check` - Verificar disponibilidad

### 📋 **Reservas (2 endpoints)**

- `POST /api/reservations` - Crear reserva **CON HORARIOS**
- `DELETE /api/reservations/{id}` - Cancelar reserva

### 👤 **Perfil de Usuario (2 endpoints)**

- `GET /api/users/profile` - Obtener perfil
- `PUT /api/users/profile` - Actualizar perfil

### ⏳ **Lista de Espera (1 endpoint)**

- `POST /api/waiting-list/claim` - Reclamar prioridad

### 👑 **Panel Administrativo (32 endpoints)**

- **Dashboard:** Métricas y resumen
- **Cabañas:** CRUD completo **CON HORARIOS**
- **Reservas:** Gestión completa
- **Usuarios:** Gestión de usuarios
- **Precios:** Calendario y historial
- **Disponibilidad:** Bloqueos y calendarios
- **Lista de Espera:** Notificaciones
- **Auditoría:** Logs del sistema
- **Configuraciones:** Parámetros del sistema

## 🔧 **Autenticación JWT**

### **Obtener Token**

```http
POST /api/auth/login
Content-Type: application/json

{
  "documentNumber": "12345678",
  "password": "password"
}
```

### **Usar Token**

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### **Registrar Nuevo Usuario**

```http
POST /api/auth/register
Content-Type: application/json

{
  "documentNumber": "99999999",
  "email": "nuevo.usuario@email.com",
  "name": "Nuevo Usuario",
  "phone": "+57-300-999-9999",
  "pin": "1234"
}
```

## 📝 **Ejemplos de Uso**

### **Crear Reserva con Horarios Personalizados**

```http
POST /api/reservations
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 2,
  "cabinId": 1,
  "startDate": "2024-03-20",
  "endDate": "2024-03-25",
  "guests": 4,
  "checkInTime": "14:00",
  "checkOutTime": "12:00"
}
```

### **Crear Cabaña con Horarios por Defecto**

```http
POST /api/admin/cabins
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "name": "Cabaña del Monte",
  "description": "Hermosa cabaña en la montaña",
  "capacity": 6,
  "bedrooms": 3,
  "bathrooms": 2,
  "basePrice": 180000.00,
  "maxGuests": 6,
  "defaultCheckInTime": "15:00",
  "defaultCheckOutTime": "11:00"
}
```

## 🚨 **Códigos de Respuesta**

### **Éxito**

- `200` - OK (operación exitosa)
- `201` - Created (recurso creado)
- `204` - No Content (operación exitosa sin contenido)

### **Error del Cliente**

- `400` - Bad Request (solicitud inválida)
- `401` - Unauthorized (no autenticado)
- `403` - Forbidden (no autorizado)
- `404` - Not Found (recurso no encontrado)
- `409` - Conflict (conflicto de datos)

### **Error del Servidor**

- `500` - Internal Server Error (error interno)

## 🔍 **Validaciones Importantes**

### **Registro Público**

- Documento debe existir en base de datos de asociados activos
- Email debe ser único en el sistema
- PIN debe tener exactamente 4 dígitos
- Nombre debe tener entre 2 y 100 caracteres

### **Reservas**

- Fechas deben ser válidas y en el futuro
- Número de huéspedes no puede exceder capacidad de la cabaña
- Debe respetar bloqueos obligatorios (reserva mínima)
- Horarios opcionales (usa horarios por defecto de la cabaña)

### **Cabañas**

- Capacidad entre 1 y 20 personas
- Precio base mayor a 0
- Horarios en formato HH:mm
- Amenidades y ubicación en formato JSON

## 📈 **Métricas de la API**

- **Total de endpoints:** 52
- **Endpoints públicos:** 15 (sin autenticación)
- **Endpoints protegidos:** 37 (requieren JWT)
- **Endpoints administrativos:** 32
- **Cobertura de funcionalidades:** 100%

## 🔄 **Actualizaciones**

### **v2.0 (Actual)**

- ✅ Registro público de usuarios
- ✅ Horarios de check-in/check-out
- ✅ Validación de documentos
- ✅ Documentación OpenAPI completa

### **v1.0 (Anterior)**

- ✅ Sistema de autenticación JWT
- ✅ Gestión de reservas básica
- ✅ Panel administrativo
- ✅ Sistema de disponibilidad

## 🛠️ **Herramientas Recomendadas**

### **Para Desarrollo**

- **Postman** - Colección completa disponible en `/postman/`
- **Swagger UI** - Interfaz web interactiva
- **Insomnia** - Cliente REST alternativo

### **Para Documentación**

- **Swagger Editor** - Editar archivos OpenAPI
- **Redoc** - Generar documentación estática
- **Swagger Codegen** - Generar clientes SDK

## 📞 **Soporte**

Para reportar problemas o solicitar nuevas funcionalidades:

- **Email:** desarrollo@cooperativa.com
- **Documentación:** `/docs/`
- **Colección Postman:** `/postman/`
- **Scripts de BD:** `/scripts/`

---

**Última actualización:** Enero 2024  
**Versión de la API:** v2.0  
**Documentación OpenAPI:** 3.0.3
