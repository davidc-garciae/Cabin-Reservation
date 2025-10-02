package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.AvailabilityApplicationService;
import com.cooperative.cabin.presentation.dto.AvailabilityDayResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/availability")
@Tag(name = "Availability", description = "Consulta de disponibilidad (público)")
public class AvailabilityController {

  private final AvailabilityApplicationService availabilityApplicationService;

  public AvailabilityController(AvailabilityApplicationService availabilityApplicationService) {
    this.availabilityApplicationService = availabilityApplicationService;
  }

  @GetMapping
  @Operation(summary = "Fechas disponibles simples", description = "Devuelve una lista de fechas disponibles en formato yyyy-MM-dd", responses = {
      @ApiResponse(responseCode = "200", description = "Lista de fechas disponibles obtenida exitosamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(type = "string", example = "2025-02-01")), examples = @ExampleObject(value = """
          [
            "2025-02-01",
            "2025-02-02",
            "2025-02-03",
            "2025-02-05",
            "2025-02-06"
          ]
          """))),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "message": "An unexpected error occurred",
            "path": "/api/availability"
          }
          """)))
  })
  public ResponseEntity<List<String>> getAvailability() {
    return ResponseEntity.ok(availabilityApplicationService.getAvailableDates());
  }

  @GetMapping("/calendar")
  @Operation(summary = "Calendario de disponibilidad", description = "Devuelve un mapa con fechas y su estado de disponibilidad para un mes específico", responses = {
      @ApiResponse(responseCode = "200", description = "Calendario de disponibilidad obtenido exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "2025-02-01": true,
            "2025-02-02": true,
            "2025-02-03": false,
            "2025-02-04": false,
            "2025-02-05": true,
            "2025-02-06": true
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Invalid year or month parameters",
            "path": "/api/availability/calendar"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "message": "An unexpected error occurred",
            "path": "/api/availability/calendar"
          }
          """)))
  })
  public ResponseEntity<Map<String, Boolean>> getCalendar(
      @Parameter(description = "Año del calendario", example = "2025") @RequestParam int year,
      @Parameter(description = "Mes del calendario (1-12)", example = "2") @RequestParam int month) {
    return ResponseEntity.ok(availabilityApplicationService.getAvailabilityCalendar(year, month));
  }

  @GetMapping("/calendar/list")
  @Operation(summary = "Calendario como lista", description = "Devuelve el calendario de disponibilidad como una lista de objetos con fecha y estado", responses = {
      @ApiResponse(responseCode = "200", description = "Lista del calendario obtenida exitosamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AvailabilityDayResponse.class)), examples = @ExampleObject(value = """
          [
            {
              "date": "2025-02-01",
              "available": true
            },
            {
              "date": "2025-02-02",
              "available": true
            },
            {
              "date": "2025-02-03",
              "available": false
            },
            {
              "date": "2025-02-04",
              "available": false
            }
          ]
          """))),
      @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Invalid year or month parameters",
            "path": "/api/availability/calendar/list"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "message": "An unexpected error occurred",
            "path": "/api/availability/calendar/list"
          }
          """)))
  })
  public ResponseEntity<List<AvailabilityDayResponse>> getCalendarList(
      @Parameter(description = "Año del calendario", example = "2025") @RequestParam int year,
      @Parameter(description = "Mes del calendario (1-12)", example = "2") @RequestParam int month) {
    Map<String, Boolean> map = availabilityApplicationService.getAvailabilityCalendar(year, month);
    List<AvailabilityDayResponse> list = map.entrySet().stream()
        .map(e -> new AvailabilityDayResponse(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(list);
  }

  // Nuevos endpoints para consulta por cabaña específica

  @GetMapping("/cabin/{cabinId}")
  @Operation(summary = "Fechas disponibles por cabaña", description = "Devuelve una lista de fechas disponibles para una cabaña específica", responses = {
      @ApiResponse(responseCode = "200", description = "Lista de fechas disponibles obtenida exitosamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(type = "string", example = "2025-02-01")), examples = @ExampleObject(value = """
          [
            "2025-02-01",
            "2025-02-02",
            "2025-02-05",
            "2025-02-06"
          ]
          """))),
      @ApiResponse(responseCode = "404", description = "Cabaña no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 404,
            "error": "Not Found",
            "message": "Cabaña no encontrada",
            "path": "/api/availability/cabin/999"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Cabaña no está activa", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Cabaña no está activa",
            "path": "/api/availability/cabin/1"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "message": "An unexpected error occurred",
            "path": "/api/availability/cabin/1"
          }
          """)))
  })
  public ResponseEntity<List<String>> getAvailabilityForCabin(
      @Parameter(description = "ID de la cabaña", example = "1") @PathVariable Long cabinId) {
    return ResponseEntity.ok(availabilityApplicationService.getAvailableDatesForCabin(cabinId));
  }

  @GetMapping("/cabin/{cabinId}/calendar")
  @Operation(summary = "Calendario de disponibilidad por cabaña", description = "Devuelve un mapa con fechas y su estado de disponibilidad para una cabaña específica en un mes", responses = {
      @ApiResponse(responseCode = "200", description = "Calendario de disponibilidad obtenido exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "2025-02-01": true,
            "2025-02-02": true,
            "2025-02-03": false,
            "2025-02-04": false,
            "2025-02-05": true,
            "2025-02-06": true
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Cabaña no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 404,
            "error": "Not Found",
            "message": "Cabaña no encontrada",
            "path": "/api/availability/cabin/999/calendar"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Parámetros inválidos o cabaña inactiva", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Cabaña no está activa",
            "path": "/api/availability/cabin/1/calendar"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "message": "An unexpected error occurred",
            "path": "/api/availability/cabin/1/calendar"
          }
          """)))
  })
  public ResponseEntity<Map<String, Boolean>> getCalendarForCabin(
      @Parameter(description = "ID de la cabaña", example = "1") @PathVariable Long cabinId,
      @Parameter(description = "Año del calendario", example = "2025") @RequestParam int year,
      @Parameter(description = "Mes del calendario (1-12)", example = "2") @RequestParam int month) {
    return ResponseEntity.ok(availabilityApplicationService.getAvailabilityCalendarForCabin(cabinId, year, month));
  }

  @GetMapping("/cabin/{cabinId}/check")
  @Operation(summary = "Verificar disponibilidad en rango", description = "Verifica si una cabaña está disponible en un rango de fechas específico", responses = {
      @ApiResponse(responseCode = "200", description = "Verificación de disponibilidad exitosa", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "available": true
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Cabaña no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 404,
            "error": "Not Found",
            "message": "Cabaña no encontrada",
            "path": "/api/availability/cabin/999/check"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Invalid date parameters",
            "path": "/api/availability/cabin/1/check"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "message": "An unexpected error occurred",
            "path": "/api/availability/cabin/1/check"
          }
          """)))
  })
  public ResponseEntity<Boolean> checkAvailability(
      @Parameter(description = "ID de la cabaña", example = "1") @PathVariable Long cabinId,
      @Parameter(description = "Fecha de inicio (yyyy-MM-dd)", example = "2025-02-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @Parameter(description = "Fecha de fin (yyyy-MM-dd)", example = "2025-02-03") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    return ResponseEntity.ok(availabilityApplicationService.isCabinAvailable(cabinId, startDate, endDate));
  }
}
