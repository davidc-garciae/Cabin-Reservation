# Cabin Reservation System

| Core                                                                                                                                                                                                                                                                          | Infra                                                                                               | API                                                                                                                                                                                                    |
| ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| ![Java](https://img.shields.io/badge/Java-21-007396?logo=java&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-6DB33F?logo=spring-boot&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?logo=gradle&logoColor=white) | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-4169E1?logo=postgresql&logoColor=white) | ![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539?logo=openapiinitiative&logoColor=white) ![Swagger](https://img.shields.io/badge/Swagger%20UI-springdoc-85EA2D?logo=swagger&logoColor=black) |

| Code                                                                                                                         | Testing                                                                             | Observability                                                                                                                                    |
| ---------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| ![MapStruct](https://img.shields.io/badge/MapStruct-1.x-02569B) ![Lombok](https://img.shields.io/badge/Lombok-1.18.x-CA0C00) | ![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white) | ![Micrometer](https://img.shields.io/badge/Micrometer-Metrics-0075A8) ![Actuator](https://img.shields.io/badge/Spring%20Actuator-Enabled-6DB33F) |

<p align="center">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="License" />
</p>

Backend de reservas de cabañas construido con Spring Boot 3.x, JWT, JPA/Hibernate, OpenAPI/Swagger, Micrometer/Actuator y Gradle. Incluye registro público de usuarios con selección de rol, horarios de check-in/check-out, gestión administrativa de documentos, endpoints públicos para catálogo/disponibilidad, endpoints autenticados para usuarios y endpoints administrativos (reservas, precios, configuraciones, disponibilidad, dashboard, documentos), DTOs estandarizados y documentación completa.

## Tabla de contenidos

- [Arquitectura y Stack](#arquitectura-y-stack)
- [Requisitos](#requisitos)
- [Configuración y perfiles](#configuración-y-perfiles)
- [Build, ejecución y pruebas](#build-ejecución-y-pruebas)
- [Autenticación y seguridad](#autenticación-y-seguridad)
- [OpenAPI y Swagger UI](#openapi-y-swagger-ui)
- [Métricas (Micrometer + Actuator)](#métricas-micrometer--actuator)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Principales endpoints](#principales-endpoints)
- [DTOs y mapeo](#dtos-y-mapeo)
- [Semillas de datos](#semillas-de-datos)
- [Pruebas](#pruebas)
- [Code Coverage](#code-coverage)
- [Próximos pasos](#próximos-pasos)
- [Troubleshooting](#troubleshooting)

---

## Arquitectura y Stack

- Spring Boot 3.x (Java 21)
- Spring Security 6 (JWT), stateless
- Spring Data JPA/Hibernate (auditoría activada)
- OpenAPI 3.0 + Swagger UI
- Micrometer + Spring Actuator
- Gradle
- MapStruct (implementación completa en PriceRange, Reservation, AvailabilityBlock)

Documentos de referencia: `TechnicalDocumentation.md`, `RefinedRequirements.md`.

## Requisitos

- Java 21 (configurado con toolchain en `build.gradle`)
- Gradle wrapper (`gradlew`/`gradlew.bat`)
- PostgreSQL (dev/prod). Tests usan H2 en perfil `test`.

## Configuración y perfiles

Perfiles:

- `!test` (runtime normal; incluye semillas de datos y auditoría)
- `test` (H2, beans mock, configuración para MVC tests)

Propiedades relevantes (application.yml/env):

- `security.jwt.secret`
- `security.jwt.accessExpirationMinutes`
- `security.jwt.refreshExpirationDays`
- `management.endpoints.web.exposure.include` (por ejemplo `health,metrics,prometheus`)

Swagger:

- `bearerAuth` (JWT) habilitado globalmente.

## Build, ejecución y pruebas

Windows PowerShell:

```bash
# Compilar
./gradlew.bat clean build

# Ejecutar app
./gradlew.bat bootRun

# Ejecutar pruebas (todas)
./gradlew.bat test

# Ejecutar una clase de test específica
./gradlew.bat test --tests "com.cooperative.cabin.presentation.controller.AdminDashboardControllerMvcTest"

# Generar reporte de coverage
./gradlew.bat test jacocoTestReport

# Ejecutar tests y verificar coverage (mínimo 80%)
./gradlew.bat test jacocoTestReport jacocoTestCoverageVerification

# Build completo con verificación de coverage
./gradlew.bat check
```

Linux/macOS:

```bash
./gradlew clean build
./gradlew bootRun
./gradlew test
./gradlew test jacocoTestReport
./gradlew check
```

Notas:

- Si ves incompatibilidad de versión de clase Java, valida que usas Java 21.
- IntelliJ: puedes ejecutar `bootRun` y tests por clase desde el IDE.
- El comando `check` ejecuta tests, genera reportes y verifica que la cobertura sea ≥80%.
- Ver sección [Code Coverage](#code-coverage) para más detalles sobre reportes y umbrales.

## Autenticación y seguridad

- JWT (Access/Refresh) gestionado por `JwtService` y `JwtAuthFilter`.
- Público: `/api/auth/**`, `/api/cabins/**`, `/api/availability**`.
- Autenticado: `/api/users/**`, `/api/reservations/**`.
- Admin (ROLE_ADMIN): `/api/admin/**`.
- Seguridad definida en `SecurityConfig` (stateless, CORS configurado para desarrollo frontend).
- CORS configurado para permitir `http://localhost:3000` y `http://localhost:3001` (frontend React/Next.js).

### Sistema de autenticación

**Usuarios normales (PROFESSOR, RETIREE):**

- Usan **PIN de 4 dígitos** tanto para registro como para login
- El PIN se almacena con hash BCrypt en la base de datos
- Pueden cambiar voluntariamente a una contraseña de 6-50 caracteres

**Administradores (ADMIN):**

- Usan **contraseña de 6-50 caracteres**
- La contraseña se almacena con hash BCrypt en la base de datos
- No pueden usar PIN de 4 dígitos (validación automática)

### Sistema de cambio de contraseña

**Endpoints disponibles:**

- `PUT /api/users/change-password` - Cambio de contraseña para usuarios autenticados
- `POST /api/admin/users/{id}/force-password-change` - Forzar cambio de contraseña (solo admin)

**Flujos soportados:**

1. **Cambio voluntario**: Cualquier usuario autenticado puede cambiar su contraseña/PIN proporcionando la contraseña actual
2. **Promoción a ADMIN**: Cuando un usuario normal es promovido a ADMIN, se marca automáticamente para cambiar su contraseña si tiene PIN de 4 dígitos
3. **Forzar cambio**: Los administradores pueden forzar que un usuario cambie su contraseña

**Validaciones:**

- Contraseña actual debe ser correcta
- Nueva contraseña debe cumplir requisitos según rol:
  - **ADMIN**: Mínimo 6 caracteres, máximo 50. No puede ser PIN de 4 dígitos
  - **PROFESSOR/RETIREE**: PIN de 4 dígitos O contraseña de 6-50 caracteres
- Nueva contraseña no puede ser igual a la actual
- Al cambiar contraseña exitosamente, se desactiva el flag de cambio obligatorio

## OpenAPI y Swagger UI

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Documentación OpenAPI**: [docs/openapi.yaml](docs/openapi.yaml)
- **Documentación Completa**: [docs/README.md](docs/README.md)

### **Nuevas Funcionalidades v2.0 Documentadas:**

- ✅ **Registro público de usuarios** (`POST /api/auth/register`) con selección de rol
- ✅ **Horarios de check-in/check-out** en reservas y cabañas
- ✅ **Validación de documentos** contra base de datos de asociados
- ✅ **Gestión administrativa de documentos** (CRUD completo)

Autorización en Swagger:

1. Clic en "Authorize".
2. Ingresa el token (puede ser solo el JWT; Swagger añade `Bearer`).

La documentación incluye descripciones, ejemplos JSON, códigos de error y validaciones por DTO.

## Métricas (Micrometer + Actuator)

- Actuator: `/actuator`, `/actuator/metrics`, `/actuator/metrics/{name}`.
- Métricas técnicas: `jvm.*`, `system.*`, `http.server.requests`, etc.
- Métricas de negocio (ver `metrics.md`):
  - `reservations.created` (counter)
  - `reservations.cancelled` (counter)
  - `reservations.status.transition{from,to}` (counter)
  - `scheduler.reservations.transition{type}` (counter)
- Dashboard admin (`GET /api/admin/dashboard`) integra:
  - Totales por estado de reserva, usuarios/cabañas activas y ocupación estimada.
  - Lectura de contadores Micrometer (normalizados a 0 si aún no existen).
- Prometheus (opcional): añadir `micrometer-registry-prometheus` y exponer `/actuator/prometheus`.

## Estructura del proyecto

```
src/main/java/com/cooperative/cabin/
├── presentation/        # Controllers y DTOs
├── application/         # Servicios de aplicación
├── domain/              # Entidades, políticas, mapeadores de dominio
├── infrastructure/      # Repositorios, config (Security, OpenAPI, Actuator)
└── common/              # Utilidades/Excepciones
```

## Principales endpoints

Autenticación (`/api/auth/*`): login, refresh, recover-password, reset-password, validate-token, register (POST) con selección de rol

Usuarios: GET/PUT `/api/users/profile`, PUT `/api/users/change-password`

Reservas: GET/POST `/api/reservations` (con horarios), GET `/api/reservations/{id}`, DELETE `/api/reservations/{id}`

Disponibilidad (público): GET `/api/availability`, GET `/api/availability/calendar`

Cabañas (público/admin): GET `/api/cabins` (con horarios por defecto), GET `/api/cabins/{id}`; Admin: POST/PUT/DELETE `/api/admin/cabins*`

Precios (admin): calendar, ranges CRUD, history, calculate

Configuraciones/Logs/Metrics (admin):

- `GET /api/admin/configurations`, `PUT /api/admin/configurations/{key}`
- `GET /api/admin/audit-logs` (paginado)
- `GET /api/admin/metrics`
- `GET /api/admin/dashboard`

Gestión de Documentos (admin):

- `GET /api/admin/documents`, `GET /api/admin/documents/active`
- `GET /api/admin/documents/number/{number}`
- `POST /api/admin/documents`
- `PUT /api/admin/documents/{id}/activate`, `PUT /api/admin/documents/{id}/deactivate`
- `DELETE /api/admin/documents/{id}`

## DTOs y mapeo

- DTOs con `@Schema` y validaciones (Jakarta Validation).
- MapStruct: implementación completa. `PriceRangeMapper`, `ReservationMapper`, `AvailabilityBlockMapper` migrados a MapStruct para optimización.

## Semillas de datos

- `DataInitializer` (perfil `!test`) inserta 5 cabañas si no hay registros.
- `SystemConfigurationsDefaultsConfig` crea configuraciones por defecto.

## Pruebas

El proyecto incluye una suite completa de tests unitarios, de integración y MVC tests para garantizar la calidad del código.

### Ejecutar Tests

```bash
# Ejecutar todos los tests
./gradlew.bat test

# Ejecutar una clase de test específica
./gradlew.bat test --tests "com.cooperative.cabin.presentation.controller.AdminDashboardControllerMvcTest"

# Ejecutar tests de un paquete específico
./gradlew.bat test --tests "com.cooperative.cabin.application.service.*"

# Ejecutar tests con información detallada
./gradlew.bat test --info
```

### Tipos de Tests

- **Tests Unitarios**: Modelos de dominio, políticas de negocio, servicios de aplicación
  - Ubicación: `src/test/java/com/cooperative/cabin/domain/`, `src/test/java/com/cooperative/cabin/application/service/`
  - Ejemplos: `DocumentNumberTest`, `PasswordValidatorTest`, `AdminDashboardServiceImplTest`

- **Tests de Integración**: Repositorios JPA con base de datos H2
  - Ubicación: `src/test/java/com/cooperative/cabin/infrastructure/repository/`
  - Ejemplos: `UserJpaRepositoryIT`, `ReservationJpaRepositoryIT`

- **MVC Tests**: Controladores REST con MockMvc
  - Ubicación: `src/test/java/com/cooperative/cabin/presentation/controller/*`
  - Perfil: `test` (H2, beans mock)
  - Ejemplos: `AdminDashboardControllerMvcTest`, `AuthControllerMvcTest`, `ReservationControllerMvcTest`

- **Tests de Seguridad**: JWT y filtros de autenticación
  - Ubicación: `src/test/java/com/cooperative/cabin/infrastructure/security/`
  - Ejemplos: `JwtServiceTest`, `JwtAuthFilterTest`

### Configuración de Tests

- **Base de datos**: H2 en memoria (perfil `test`)
- **Mock beans**: Configuración en `TestMvcConfiguration`, `TestBeansConfiguration`
- **Auditoría**: Mockeada en `TestAuditingConfiguration`
- **Seguridad**: `@WithMockUser` para simular usuarios autenticados

## Code Coverage

El proyecto utiliza **JaCoCo** para medir la cobertura de código y garantiza un mínimo del **80% de cobertura de líneas**.

### Generar Reporte de Coverage

```bash
# Ejecutar tests y generar reporte de coverage
./gradlew.bat test jacocoTestReport

# Ejecutar tests, generar reporte y verificar umbral mínimo (80%)
./gradlew.bat test jacocoTestReport jacocoTestCoverageVerification

# El comando `check` incluye automáticamente la verificación de coverage
./gradlew.bat check
```

### Ver Reportes

Los reportes de coverage están disponibles en formato HTML y CSV:

- **HTML (recomendado)**: `build/reports/jacoco/test/html/index.html`
- **CSV**: `build/reports/jacoco/test/jacocoTestReport.csv`
- **XML**: `build/reports/jacoco/test/jacocoTestReport.xml`

Abre el archivo HTML en tu navegador para ver:
- Cobertura por paquete, clase y método
- Líneas cubiertas vs. no cubiertas
- Métricas de instrucciones, ramas y complejidad
- Visualización interactiva del código fuente

### Umbral Mínimo

El proyecto requiere un **mínimo del 80% de cobertura de líneas**. La verificación se ejecuta automáticamente con:

- `jacocoTestCoverageVerification` (tarea independiente)
- `check` (incluye verificación de coverage)

Si la cobertura está por debajo del 80%, el build fallará con un mensaje indicando el porcentaje actual.

### Exclusiones de Coverage

Para mantener el umbral alcanzable, se excluyen del cálculo de coverage:

- **Configuraciones**: Clases de configuración de Spring (`*Config`, `*Configuration`)
- **DTOs**: Clases de transferencia de datos (`*Request`, `*Response`, `*Dto`)
- **Mappers**: Interfaces de MapStruct (`*Mapper`)
- **Aplicación principal**: `CabinReservationApplication`
- **Excepciones**: Clases de excepciones de dominio
- **Algunos controladores**: Controladores con lógica mínima o delegación directa
- **Algunos servicios**: Servicios con alta complejidad o dependencias externas pesadas

Las exclusiones están configuradas en `build.gradle` bajo `jacocoExcludes`.

### Mejorar Coverage

Si necesitas mejorar la cobertura:

1. Revisa el reporte HTML para identificar clases con baja cobertura
2. Añade tests unitarios para métodos no cubiertos
3. Añade casos de prueba para ramas condicionales
4. Considera añadir tests de integración para flujos complejos
5. Ejecuta `./gradlew.bat test jacocoTestReport` para ver el progreso

### Estadísticas Actuales

- **Cobertura mínima requerida**: 80% de líneas
- **Tipos de tests**: Unitarios, integración, MVC, seguridad
- **Base de datos de tests**: H2 en memoria
- **Framework de testing**: JUnit 5 + Mockito + Spring Test

## Próximos pasos

- Notificaciones (mock):
  - POST `/api/admin/notifications/send`
  - GET `/api/admin/notifications/history`
  - POST `/api/admin/waiting-list/{id}/notify`
- Migración de mappers restantes a MapStruct (opcional - mappers manuales funcionan correctamente).
- Integración Prometheus/Grafana.

## Troubleshooting

- Versión Java: usa Java 21 o ajusta toolchain.
- Swagger sin “Authorize”: confirma `SwaggerConfig` con `bearerAuth` y permisos en `SecurityConfig`.
- Tests y seguridad: en MVC tests públicos usa `@AutoConfigureMockMvc(addFilters=false)` o mockea `JwtAuthFilter` cuando pruebes la cadena completa.

---

Hecho con <3 por David García
