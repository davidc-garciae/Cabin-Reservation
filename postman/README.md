# 🚀 Colección de Postman - API de Reservas de Cabañas

Esta colección contiene todos los endpoints de la API de reservas de cabañas organizados por funcionalidad, con ejemplos reales usando los datos de prueba.

## 📁 Archivos Incluidos

- **`Cabin-Reservation-API.postman_collection.json`** - Colección principal con todos los endpoints
- **`Cabin-Reservation-Environment.postman_environment.json`** - Variables de entorno para configuración
- **`README.md`** - Este archivo con instrucciones

## 🛠️ Configuración Inicial

### 1. Importar en Postman

1. Abre Postman
2. Click en **Import**
3. Selecciona los archivos:
   - `Cabin-Reservation-API.postman_collection.json`
   - `Cabin-Reservation-Environment.postman_environment.json`
4. Click en **Import**

### 2. Configurar Entorno

1. En la esquina superior derecha, selecciona el entorno: **"Cabin Reservation - Local Development"**
2. Verifica que la variable `base_url` esté configurada como `http://localhost:8080`

### 3. Verificar Aplicación

Asegúrate de que tu aplicación esté ejecutándose en `http://localhost:8080` y que la base de datos esté poblada con los datos de prueba.

## 🎯 Flujo de Pruebas Recomendado

### Paso 1: Autenticación

1. **Login - Admin** → Obtiene tokens de administrador (usa contraseña: `Admin#12345`)
2. **Login - Profesor** → Obtiene tokens de profesor (usa PIN: `1234`)
3. **Login - Pensionado** → Obtiene tokens de pensionado (usa PIN: `1234`)
4. **Register New User** → Registra nuevo usuario y obtiene tokens (PIN de 4 dígitos requerido)

**Importante:**

- Los administradores usan contraseña de 6-50 caracteres
- Los usuarios normales (PROFESSOR, RETIREE) usan PIN de 4 dígitos

### Paso 2: Consultas Públicas

1. **Get All Cabins** → Ver todas las cabañas disponibles
2. **Get Cabin by ID** → Ver detalles de una cabaña específica
3. **Search Cabins** → Buscar cabañas por criterios
4. **Get Available Dates** → Ver fechas disponibles (todas las cabañas)
5. **Get Availability Calendar** → Ver calendario de disponibilidad (todas las cabañas)
6. **Get Available Dates for Specific Cabin** → Ver fechas disponibles por cabaña específica
7. **Get Availability Calendar for Specific Cabin** → Ver calendario por cabaña específica
8. **Check Availability for Date Range** → Verificar disponibilidad en rango específico

### Paso 3: Gestión de Perfil

1. **Get User Profile** → Ver perfil del usuario (requiere autenticación)
2. **Update User Profile** → Actualizar datos del perfil
3. **Change Password** → Cambiar contraseña o PIN (requiere autenticación)

### Paso 4: Reservas

1. **Create Reservation** → Crear nueva reserva (con horarios personalizados)
2. **Create Reservation (Testing Block)** → Probar bloqueos obligatorios
3. **Create Reservation (Partial Block - Should Fail)** → Verificar validaciones

### Paso 5: Funcionalidades Administrativas

1. **Get Dashboard Summary** → Ver resumen del sistema
2. **List All Reservations** → Ver todas las reservas
3. **Notify Next in Queue** → Gestionar lista de espera
4. **Get Pricing Calendar (Real Data)** → Ver calendario de precios con datos reales
5. **Get Pricing History (Real Data)** → Ver historial completo de cambios de precios
6. **Calculate Price for Specific Date** → Calcular precio real para fecha específica

## 📊 Datos de Prueba Disponibles

### 👥 Usuarios

| Rol        | Documento | Contraseña/PIN | Email                        | Nota                                |
| ---------- | --------- | -------------- | ---------------------------- | ----------------------------------- |
| Admin      | 12345678  | Admin#12345    | admin@cooperativa.com        | Administrador usa contraseña        |
| Profesor   | 87654321  | 1234           | profesor1@universidad.edu.co | Usuario normal usa PIN de 4 dígitos |
| Profesor   | 11223344  | 1234           | profesor2@universidad.edu.co | Usuario normal usa PIN de 4 dígitos |
| Pensionado | 99887766  | 1234           | pensionado1@email.com        | Usuario normal usa PIN de 4 dígitos |
| Nuevo      | 99999999  | 1234           | nuevo.usuario@email.com      | Usuario normal usa PIN de 4 dígitos |

**Nota importante sobre autenticación:**

- **Administradores (ADMIN)**: Usan contraseña de 6-50 caracteres (ejemplo: `Admin#12345`)
- **Usuarios normales (PROFESSOR, RETIREE)**: Usan PIN de exactamente 4 dígitos (ejemplo: `1234`)

### 🏠 Cabañas

| ID  | Nombre            | Capacidad | Precio Base |
| --- | ----------------- | --------- | ----------- |
| 1   | Cabaña del Bosque | 6         | $150,000    |
| 2   | Cabaña del Lago   | 4         | $120,000    |
| 3   | Cabaña Familiar   | 8         | $200,000    |
| 4   | Cabaña Romántica  | 2         | $80,000     |

### 🚫 Bloqueos de Disponibilidad

- **Cabaña del Bosque (15-17 Marzo):** Reserva mínima de 3 días
- **Cabaña del Lago (1-3 Abril):** Reserva mínima de 3 días
- **Cabaña Familiar (15-22 Junio):** Reserva mínima de 7 días

## 🔧 Características de la Colección

### ✨ Autenticación Automática

- Los endpoints de login guardan automáticamente los tokens en variables
- Los endpoints protegidos usan automáticamente los tokens correspondientes

### 📝 Variables Dinámicas

- Generación automática de fechas para testing
- IDs de usuarios y cabañas preconfigurados
- URLs y configuraciones centralizadas

### 🧪 Tests Automáticos

- Validación de códigos de respuesta
- Guardado automático de tokens
- Logs de información útil

### 📚 Documentación Completa

- Descripción detallada de cada endpoint
- Ejemplos de request y response
- Casos de uso específicos

## 🎯 Casos de Prueba Específicos

### 1. Probar Funcionalidad Real de Availability

```
✅ GET /api/availability/cabin/1
# Devuelve fechas disponibles reales para Cabaña del Bosque

✅ GET /api/availability/cabin/1/calendar?year=2024&month=3
# Devuelve calendario real basado en reservas y bloques

✅ GET /api/availability/cabin/1/check?startDate=2024-03-20&endDate=2024-03-25
# Verifica disponibilidad real en rango específico

❌ GET /api/availability/cabin/1/check?startDate=2024-03-15&endDate=2024-03-16
# Debe devolver false por reserva parcial en bloqueo obligatorio
```

### 1.1. Probar Funcionalidad Real de Pricing

```
✅ GET /api/admin/pricing/calendar/2024/3
# Devuelve calendario de precios con datos reales de la base de datos

✅ GET /api/admin/pricing/history
# Devuelve historial completo de cambios de precios con datos reales

✅ GET /api/admin/pricing/calculate?cabinId=1&date=2024-03-15
# Calcula precio real para fecha específica basado en rangos de precios
```

### 2. Probar Bloqueos Obligatorios

```
✅ POST /api/reservations
{
  "userId": 2,
  "cabinId": 1,
  "startDate": "2024-03-15",
  "endDate": "2024-03-17",  // Exacto del bloqueo
  "guests": 4,
  "checkInTime": "15:00",
  "checkOutTime": "11:00"
}

❌ POST /api/reservations
{
  "userId": 2,
  "cabinId": 1,
  "startDate": "2024-03-15",
  "endDate": "2024-03-16",  // Parcial del bloqueo
  "guests": 4,
  "checkInTime": "15:00",
  "checkOutTime": "11:00"
}
```

### 3. Probar Registro Público

```
✅ POST /api/auth/register
{
  "documentNumber": "99999999",
  "email": "nuevo.usuario@email.com",
  "name": "Nuevo Usuario",
  "phone": "+57-300-999-9999",
  "pin": "1234"
}
```

### 4. Probar Diferentes Roles

- **Admin:** Acceso completo a todos los endpoints
- **Profesor:** Acceso a funciones de usuario + algunas administrativas
- **Pensionado:** Acceso solo a funciones de usuario

### 5. Probar Validaciones

- Fechas inválidas
- Capacidad excedida
- Usuarios inactivos
- Tokens expirados

## 🚨 Troubleshooting

### Error 401 (Unauthorized)

- Verificar que el token esté configurado correctamente
- Verificar que el usuario esté activo en la base de datos
- Verificar que el token no haya expirado

### Error 403 (Forbidden)

- Verificar que el usuario tenga el rol correcto
- Verificar que el endpoint requiera autenticación

### Error 404 (Not Found)

- Verificar que el recurso exista en la base de datos
- Verificar que los IDs sean correctos

### Error 409 (Conflict)

- Verificar disponibilidad de fechas
- Verificar bloqueos obligatorios
- Verificar límites de reservas

## 📈 Métricas y Monitoreo

La colección incluye endpoints para verificar:

- Estado general del sistema (dashboard)
- Número de reservas por estado
- Ocupación de cabañas
- Usuarios activos

## 🔄 Actualizaciones

Para mantener la colección actualizada:

1. Revisar cambios en los controladores
2. Actualizar ejemplos con nuevos datos de prueba
3. Agregar nuevos endpoints según sea necesario
4. Actualizar documentación de casos de uso

## 📞 Soporte

Si encuentras problemas:

1. Verificar que la aplicación esté ejecutándose
2. Verificar que la base de datos esté poblada
3. Revisar logs de la aplicación
4. Verificar configuración de variables de entorno
