# Scripts de Base de Datos

Este directorio contiene scripts SQL para gestionar la base de datos de la aplicación de reservas de cabañas.

## 📁 Archivos Disponibles

### 🗑️ `cleanup-database.sql`

**Propósito:** Limpia completamente la base de datos eliminando todos los datos.

**⚠️ ADVERTENCIA:** Este script elimina TODOS los datos de la base de datos. Solo usar en entornos de desarrollo/testing.

**Qué hace:**

- Elimina todos los datos de todas las tablas
- Reinicia las secuencias de auto-incremento
- Deshabilita temporalmente las restricciones de clave foránea

### 📊 `insert-test-data.sql`

**Propósito:** Inserta datos de prueba completos en la base de datos.

**Qué incluye:**

- 6 usuarios (1 admin, 3 profesores, 2 pensionados)
- 4 cabañas con diferentes características
- 6 números de documento activos
- 10 configuraciones del sistema
- 3 rangos de precios especiales
- 3 bloqueos de disponibilidad (reservas mínimas obligatorias)
- 3 reservas de ejemplo
- 3 entradas en lista de espera
- 3 logs de auditoría de ejemplo

### ⚙️ `insert-system-configs-only.sql`

**Propósito:** Inserta solo las configuraciones del sistema sin afectar otros datos.

**Útil cuando:**

- Solo necesitas resetear las configuraciones
- Quieres mantener los datos existentes
- Necesitas actualizar configuraciones después de cambios en el código

## 🚀 Cómo Usar los Scripts

### Opción 1: Usando pgAdmin

1. Abre pgAdmin
2. Conecta a tu base de datos
3. Abre la consola SQL (Query Tool)
4. Copia y pega el contenido del script
5. Ejecuta el script (F5 o botón Execute)

### Opción 2: Usando línea de comandos

```bash
# Limpiar base de datos
psql -h localhost -p 5433 -U postgres -d cabin-reservation -f cleanup-database.sql

# Insertar datos de prueba
psql -h localhost -p 5433 -U postgres -d cabin-reservation -f insert-test-data.sql

# Solo configuraciones del sistema
psql -h localhost -p 5433 -U postgres -d cabin-reservation -f insert-system-configs-only.sql
```

### Opción 3: Desde la aplicación Spring Boot

Puedes ejecutar los scripts directamente desde la aplicación usando:

```java
@Sql(scripts = "/scripts/cleanup-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
```

## 📋 Flujo Recomendado

### Para Desarrollo/Testing:

1. **Limpiar:** `cleanup-database.sql`
2. **Poblar:** `insert-test-data.sql`
3. **Probar:** Ejecutar la aplicación y probar funcionalidades

### Para Resetear Solo Configuraciones:

1. **Resetear configs:** `insert-system-configs-only.sql`

### Para Producción:

❌ **NO usar estos scripts en producción**

- Las configuraciones del sistema se crean automáticamente al iniciar la aplicación
- Usar herramientas de migración apropiadas para producción

## 🔐 Credenciales de Usuarios de Prueba

| Rol        | Email                        | Contraseña | Documento |
| ---------- | ---------------------------- | ---------- | --------- |
| Admin      | admin@cooperativa.com        | password   | 12345678  |
| Profesor   | profesor1@universidad.edu.co | password   | 87654321  |
| Profesor   | profesor2@universidad.edu.co | password   | 11223344  |
| Profesor   | profesor3@universidad.edu.co | password   | 55667788  |
| Pensionado | pensionado1@email.com        | password   | 99887766  |
| Pensionado | pensionado2@email.com        | password   | 44332211  |

**Nota:** Todos los usuarios tienen la contraseña `password` (hash: `$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi`)

## 🏠 Cabañas Disponibles

1. **Cabaña del Bosque** - Capacidad: 6 personas, Precio: $150,000
2. **Cabaña del Lago** - Capacidad: 4 personas, Precio: $120,000
3. **Cabaña Familiar** - Capacidad: 8 personas, Precio: $200,000
4. **Cabaña Romántica** - Capacidad: 2 personas, Precio: $80,000

## 🚫 Bloqueos de Disponibilidad (Reservas Mínimas Obligatorias)

Los `AvailabilityBlock` funcionan como **reservas mínimas obligatorias**. Si hay un bloqueo en ciertas fechas, los usuarios deben reservar **mínimamente todo el bloqueo completo**.

**✅ FUNCIONALIDAD REAL IMPLEMENTADA**:

- El sistema de availability ahora consulta datos reales de la base de datos, no datos simulados.
- El sistema de pricing ahora calcula precios reales basados en rangos de precios de la base de datos.

### Ejemplos en los Datos de Prueba:

1. **Cabaña del Bosque (15-17 Marzo):**

   - ❌ No se puede reservar solo el 15 de marzo
   - ❌ No se puede reservar solo el 16 de marzo
   - ✅ Se puede reservar del 15 al 17 de marzo (exacto)
   - ✅ Se puede reservar del 15 al 20 de marzo (incluye el bloqueo)

2. **Cabaña del Lago (1-3 Abril):**

   - ❌ No se puede reservar solo el 1 de abril
   - ✅ Se puede reservar del 1 al 3 de abril (exacto)
   - ✅ Se puede reservar del 1 al 5 de abril (incluye el bloqueo)

3. **Cabaña Familiar (15-22 Junio):**
   - ❌ No se puede reservar solo el 15 de junio
   - ✅ Se puede reservar del 15 al 22 de junio (exacto)
   - ✅ Se puede reservar del 15 al 30 de junio (incluye el bloqueo)

### Casos de Uso Reales:

- **Fines de semana largos:** Forzar reservas mínimas de 3 días
- **Puentes festivos:** Evitar reservas de un solo día
- **Temporadas altas:** Requerir reservas mínimas de una semana
- **Eventos especiales:** Asegurar ocupación completa durante eventos

## ⚠️ Consideraciones Importantes

- Los scripts están diseñados para PostgreSQL
- Las fechas están configuradas para 2024
- Los precios están en pesos colombianos
- Los scripts incluyen manejo de secuencias de auto-incremento
- Se respetan las restricciones de clave foránea

## 🔧 Personalización

Puedes modificar los scripts para:

- Cambiar fechas según tus necesidades
- Ajustar precios
- Agregar más usuarios o cabañas
- Modificar configuraciones del sistema
- Cambiar ubicaciones geográficas
