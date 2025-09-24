# Configuración de Entorno y Ejecución

Este documento explica cómo configurar variables de entorno y ejecutar el proyecto Cabin Reservation System en entornos locales (Windows/Linux/macOS).

## Requisitos previos

- Java 21 (JDK)
- Gradle Wrapper (incluido: `gradlew`/`gradlew.bat`)
- PostgreSQL 14+ (desarrollo/producción)

## Variables de entorno

Define estas variables para configurar autenticación, base de datos y Actuator.

Autenticación (JWT):

- `SECURITY_JWT_SECRET` (requerida): Secreto para firmar JWT. Puede ser texto plano; el servicio lo codifica internamente si no está en Base64.
- `SECURITY_JWT_ACCESS_EXPIRATION_MINUTES` (opcional, por defecto 15): Minutos de expiración del access token.
- `SECURITY_JWT_REFRESH_EXPIRATION_DAYS` (opcional, por defecto 7): Días de expiración del refresh token.

Base de datos (PostgreSQL):

- `SPRING_DATASOURCE_URL` (requerida en dev/prod): Ej. `jdbc:postgresql://localhost:5432/cabin`
- `SPRING_DATASOURCE_USERNAME` (requerida)
- `SPRING_DATASOURCE_PASSWORD` (requerida)

Actuator / Observabilidad:

- `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` (opcional): Ej. `health,info,metrics,prometheus`

Servidor:

- `SERVER_PORT` (opcional): Puerto HTTP (por defecto 8080)

Perfiles:

- `SPRING_PROFILES_ACTIVE` (opcional): `dev` (implícito) o `test`.

## Ejemplos de configuración

### Windows (PowerShell)

```powershell
# JWT
[System.Environment]::SetEnvironmentVariable("SECURITY_JWT_SECRET","mi-secreto-super-seguro","User")
[System.Environment]::SetEnvironmentVariable("SECURITY_JWT_ACCESS_EXPIRATION_MINUTES","15","User")
[System.Environment]::SetEnvironmentVariable("SECURITY_JWT_REFRESH_EXPIRATION_DAYS","7","User")

# PostgreSQL
[System.Environment]::SetEnvironmentVariable("SPRING_DATASOURCE_URL","jdbc:postgresql://localhost:5432/cabin","User")
[System.Environment]::SetEnvironmentVariable("SPRING_DATASOURCE_USERNAME","postgres","User")
[System.Environment]::SetEnvironmentVariable("SPRING_DATASOURCE_PASSWORD","postgres","User")

# Actuator opcional
[System.Environment]::SetEnvironmentVariable("MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE","health,info,metrics,prometheus","User")
```

### Linux/macOS (bash)

```bash
# JWT
export SECURITY_JWT_SECRET="mi-secreto-super-seguro"
export SECURITY_JWT_ACCESS_EXPIRATION_MINUTES=15
export SECURITY_JWT_REFRESH_EXPIRATION_DAYS=7

# PostgreSQL
env | grep SPRING_DATASOURCE || true
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/cabin"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="postgres"

# Actuator opcional
export MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE="health,info,metrics,prometheus"
```

## application.yml (referencia)

El proyecto usa variables de entorno. Ejemplo orientativo:

```yaml
server:
  port: ${SERVER_PORT:8080}

security:
  jwt:
    secret: ${SECURITY_JWT_SECRET}
    accessExpirationMinutes: ${SECURITY_JWT_ACCESS_EXPIRATION_MINUTES:15}
    refreshExpirationDays: ${SECURITY_JWT_REFRESH_EXPIRATION_DAYS:7}

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update

management:
  endpoints:
    web:
      exposure:
        include: ${MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE:health,info,metrics}
```

## Ejecución

Windows (PowerShell):

```powershell
./gradlew.bat clean build
./gradlew.bat bootRun
```

Linux/macOS:

```bash
./gradlew clean build
./gradlew bootRun
```

Aplicación en: `http://localhost:8080`

## Swagger / OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Authorize (JWT):

1. Clic en “Authorize”.
2. Ingresa el token (sólo el JWT; Swagger añade `Bearer`).

## Actuator y Métricas

- Actuator base: `/actuator`
- Métricas: `/actuator/metrics`
- Detalle: `/actuator/metrics/{metricName}`
- (Opcional) Prometheus: añade `micrometer-registry-prometheus` y expón `/actuator/prometheus`.

## Perfiles de prueba

- Perfil `test` usa H2 y beans mock para MVC tests. No requiere PostgreSQL.
- Ejecuta pruebas: `./gradlew test`

## Problemas comunes

- Java incompatible: usa JDK 21 o ajusta toolchain en `build.gradle`.
- Swagger sin “Authorize”: confirma `SwaggerConfig` con `bearerAuth` y permisos en `SecurityConfig`.
- 403 en endpoints admin: necesitas rol ADMIN o token válido.
- Errores de conexión DB: valida `SPRING_DATASOURCE_*`.

## Generación de un secreto JWT

- Usa un string suficientemente largo y aleatorio.
- Ejemplo (Linux/macOS): `openssl rand -base64 48`
- Ejemplo (PowerShell): `[Convert]::ToBase64String((1..48 | ForEach-Object {Get-Random -Max 256}))`

---

Para más detalles, revisa `README.md`, `ImplementationPlan.md`, `TechnicalDocumentation.md` y `metrics.md`.
