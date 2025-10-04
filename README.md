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

Backend de reservas de cabañas construido con Spring Boot 3.x, JWT, JPA/Hibernate, OpenAPI/Swagger, Micrometer/Actuator y Gradle. Incluye endpoints públicos para catálogo/disponibilidad, endpoints autenticados para usuarios y endpoints administrativos (reservas, precios, configuraciones, disponibilidad, dashboard), DTOs estandarizados y documentación completa.

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
```

Linux/macOS:

```bash
./gradlew clean build
./gradlew bootRun
./gradlew test
```

Notas:

- Si ves incompatibilidad de versión de clase Java, valida que usas Java 21.
- IntelliJ: puedes ejecutar `bootRun` y tests por clase desde el IDE.

## Autenticación y seguridad

- JWT (Access/Refresh) gestionado por `JwtService` y `JwtAuthFilter`.
- Público: `/api/auth/**`, `/api/cabins/**`, `/api/availability**`.
- Autenticado: `/api/users/**`, `/api/reservations/**`.
- Admin (ROLE_ADMIN): `/api/admin/**`.
- Seguridad definida en `SecurityConfig` (stateless, CORS configurado para desarrollo frontend).
- CORS configurado para permitir `http://localhost:3000` y `http://localhost:3001` (frontend React/Next.js).

## OpenAPI y Swagger UI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Autorización en Swagger:

1. Clic en “Authorize”.
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

Autenticación (`/api/auth/*`): login, refresh, recover-password, reset-password, validate-token (POST)

Usuarios: GET/PUT `/api/users/profile`

Reservas: GET/POST `/api/reservations`, GET `/api/reservations/{id}`, DELETE `/api/reservations/{id}`

Disponibilidad (público): GET `/api/availability`, GET `/api/availability/calendar`

Cabañas (público/admin): GET `/api/cabins`, GET `/api/cabins/{id}`; Admin: POST/PUT/DELETE `/api/admin/cabins*`

Precios (admin): calendar, ranges CRUD, history, calculate

Configuraciones/Logs/Metrics (admin):

- `GET /api/admin/configurations`, `PUT /api/admin/configurations/{key}`
- `GET /api/admin/audit-logs` (paginado)
- `GET /api/admin/metrics`
- `GET /api/admin/dashboard`

## DTOs y mapeo

- DTOs con `@Schema` y validaciones (Jakarta Validation).
- MapStruct: implementación completa. `PriceRangeMapper`, `ReservationMapper`, `AvailabilityBlockMapper` migrados a MapStruct para optimización.

## Semillas de datos

- `DataInitializer` (perfil `!test`) inserta 5 cabañas si no hay registros.
- `SystemConfigurationsDefaultsConfig` crea configuraciones por defecto.

## Pruebas

- MVC tests en `src/test/java/com/cooperative/cabin/presentation/controller/*` (perfil `test`, H2, beans mock).
- `GlobalExceptionHandler` homogeniza 400/403/404/500.
- Dashboard: testea 200 (ADMIN), 403 (USER) y estructura JSON.

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
