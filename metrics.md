# Métricas (Micrometer + Actuator)

## Endpoints

- Actuator base: `/actuator`
- Lista de métricas: `/actuator/metrics`
- Detalle métrica: `/actuator/metrics/{metricName}`

En `application.yml` ya está expuesto `management.endpoints.web.exposure.include: "*"`.

## Métricas técnicas (automáticas)

- `jvm.*`, `system.*`, `process.*`
- HTTP: `http.server.requests`
- DB/conexiones si el datasource lo soporta

## Métricas de negocio (implementadas)

- `reservations.created` (Counter) ✅
  - Incrementa al crear una pre-reserva.
- `reservations.cancelled` (Counter) ✅
  - Incrementa al cancelar una reserva (usuario).
- `reservations.status.transition{from,to}` (Counter con tags) ✅
  - Incrementa en cambios de estado por admin.
  - Tags: `from` (PENDING, CONFIRMED, IN_USE, …), `to`.
- `scheduler.reservations.transition{type}` (Counter con tags) ✅
  - Incrementa en transiciones automáticas del scheduler.
  - Tags: `type` = `start` (CONFIRMED→IN_USE) | `end` (IN_USE→COMPLETED).

## Ejemplos de consulta

- Listar todo: `GET /actuator/metrics`
- Ver creadas: `GET /actuator/metrics/reservations.created`
- Ver transición por estado:
  - `GET /actuator/metrics/reservations.status.transition`
  - Filtrar por tags, p. ej. en UI de Spring Boot Admin/Prometheus/Grafana.

## Integración Prometheus (opcional)

- Añadir dependencia `micrometer-registry-prometheus`.
- Exponer `/actuator/prometheus`.
- Configurar scrape en Prometheus; graficar en Grafana.

## Ubicación en código

- Incrementos en `ReservationApplicationService` (crear/cancelar/cambio estado). ✅
- Incrementos del scheduler en `SchedulingApplicationService`. ✅
- Implementado en `BusinessMetrics` con `MeterRegistry`. ✅

## Buenas prácticas

- Nombres estables en snake-case o dotted (usamos dotted).
- Tags cortos y con cardinalidad controlada.
- Evitar gauges arbitrarios; preferir counters/timers.
