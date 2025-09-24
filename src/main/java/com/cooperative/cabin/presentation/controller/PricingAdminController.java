package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.PricingApplicationService;
import com.cooperative.cabin.domain.model.PriceRange;
import com.cooperative.cabin.presentation.dto.PatchPriceRangeRequest;
import com.cooperative.cabin.presentation.dto.PriceRangeResponse;
import com.cooperative.cabin.presentation.mapper.PriceRangeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.cooperative.cabin.presentation.dto.PriceChangeResponse;

@RestController
@RequestMapping("/api/admin/pricing")
@Tag(name = "Pricing Admin", description = "Gestión de rangos de precios")
@SecurityRequirement(name = "bearerAuth")
public class PricingAdminController {

    private final PricingApplicationService pricingApplicationService;

    public PricingAdminController(PricingApplicationService pricingApplicationService) {
        this.pricingApplicationService = pricingApplicationService;
    }

    @PatchMapping("/ranges/{id}")
    @Operation(summary = "Actualización parcial de rango de precios", responses = @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PriceRangeResponse.class))))
    public ResponseEntity<PriceRangeResponse> patch(@PathVariable("id") Long id,
            @RequestBody PatchPriceRangeRequest request) {
        PriceRange updated = pricingApplicationService.partialUpdatePriceRange(
                id,
                request.getBasePrice(),
                request.getMultiplier(),
                request.getStartDate(),
                request.getEndDate());
        PriceRangeResponse response = PriceRangeMapper.INSTANCE.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ranges")
    @Operation(summary = "Listar todos los rangos de precios", description = "Obtiene todos los rangos de precios configurados en el sistema", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de rangos obtenida exitosamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PriceRangeResponse.class)), examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "cabinId": 1,
                        "startDate": "2024-02-01",
                        "endDate": "2024-02-29",
                        "basePrice": 150.00,
                        "multiplier": 1.2
                      },
                      {
                        "id": 2,
                        "cabinId": 1,
                        "startDate": "2024-03-01",
                        "endDate": "2024-03-31",
                        "basePrice": 180.00,
                        "multiplier": 1.5
                      }
                    ]
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/pricing/ranges"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 500,
                      "error": "Internal Server Error",
                      "message": "An unexpected error occurred",
                      "path": "/api/admin/pricing/ranges"
                    }
                    """)))
    })
    public ResponseEntity<List<PriceRangeResponse>> list() {
        List<PriceRange> list = pricingApplicationService.listPriceRanges();
        List<PriceRangeResponse> dto = list.stream().map(PriceRangeMapper.INSTANCE::toResponse).toList();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/ranges")
    @Operation(summary = "Crear nuevo rango de precios", description = "Crea un nuevo rango de precios para una cabaña en fechas específicas", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del rango de precios a crear", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatePriceRangeRequest.class), examples = @ExampleObject(value = """
            {
              "cabinId": 1,
              "startDate": "2024-02-01",
              "endDate": "2024-02-29",
              "basePrice": 150.00,
              "multiplier": 1.2
            }
            """))), responses = {
            @ApiResponse(responseCode = "201", description = "Rango de precios creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PriceRange.class), examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "cabinId": 1,
                      "startDate": "2024-02-01",
                      "endDate": "2024-02-29",
                      "basePrice": 150.00,
                      "multiplier": 1.2
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Invalid date range or price values",
                      "path": "/api/admin/pricing/ranges"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/pricing/ranges"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Cabaña no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Cabin not found with id: 1",
                      "path": "/api/admin/pricing/ranges"
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "Conflicto - Rango de fechas ya existe", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Price range already exists for this date period",
                      "path": "/api/admin/pricing/ranges"
                    }
                    """)))
    })
    public ResponseEntity<PriceRange> create(@RequestBody CreatePriceRangeRequest request) {
        PriceRange created = pricingApplicationService.createPriceRange(
                request.getCabinId(), request.getStartDate(), request.getEndDate(), request.getBasePrice(),
                request.getMultiplier());
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(created);
    }

    @GetMapping("/calendar/{year}/{month}")
    @Operation(summary = "Calendario de precios", responses = @ApiResponse(responseCode = "200", description = "OK"))
    public ResponseEntity<Map<String, java.math.BigDecimal>> calendar(@PathVariable int year, @PathVariable int month) {
        return ResponseEntity.ok(pricingApplicationService.getCalendar(year, month));
    }

    @GetMapping("/history")
    @Operation(summary = "Historial de cambios de precio")
    public ResponseEntity<List<PriceChangeResponse>> history() {
        List<Map<String, Object>> rows = pricingApplicationService.getHistory();
        List<PriceChangeResponse> dto = rows.stream().map(row -> new PriceChangeResponse(
                ((Number) row.get("id")).longValue(),
                ((Number) row.get("cabinId")).longValue(),
                String.valueOf(row.get("date")),
                String.valueOf(row.get("oldPrice")),
                String.valueOf(row.get("newPrice")))).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/calculate")
    @Operation(summary = "Calcular precio para día")
    public ResponseEntity<Map<String, java.math.BigDecimal>> calculate(@RequestParam Long cabinId,
            @RequestParam java.time.LocalDate date) {
        java.math.BigDecimal price = pricingApplicationService.calculatePrice(cabinId, date);
        return ResponseEntity.ok(java.util.Map.of("price", price));
    }

    @Schema(description = "Solicitud de creación de rango de precios")
    public static class CreatePriceRangeRequest {
        @Schema(description = "ID de la cabaña", example = "1")
        private Long cabinId;

        @Schema(description = "Fecha de inicio del rango", example = "2024-02-01")
        private LocalDate startDate;

        @Schema(description = "Fecha de fin del rango", example = "2024-02-29")
        private LocalDate endDate;

        @Schema(description = "Precio base del rango", example = "150.00")
        private java.math.BigDecimal basePrice;

        @Schema(description = "Multiplicador de precio", example = "1.2")
        private java.math.BigDecimal multiplier;

        public Long getCabinId() {
            return cabinId;
        }

        public void setCabinId(Long cabinId) {
            this.cabinId = cabinId;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public java.math.BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(java.math.BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public java.math.BigDecimal getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(java.math.BigDecimal multiplier) {
            this.multiplier = multiplier;
        }
    }

    @PutMapping("/ranges/{id}")
    public ResponseEntity<PriceRange> update(@PathVariable("id") Long id,
            @RequestBody CreatePriceRangeRequest request) {
        PriceRange updated = pricingApplicationService.updatePriceRange(
                id, request.getCabinId(), request.getStartDate(), request.getEndDate(), request.getBasePrice(),
                request.getMultiplier());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/ranges/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        pricingApplicationService.deletePriceRange(id);
        return ResponseEntity.noContent().build();
    }
}
