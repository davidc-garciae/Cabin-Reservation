package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.CabinApplicationService;
import com.cooperative.cabin.presentation.dto.CabinResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cabins")
@Tag(name = "Cabañas", description = "Consulta de cabañas disponibles (público)")
public class CabinController {

  private final CabinApplicationService cabinApplicationService;

  public CabinController(CabinApplicationService cabinApplicationService) {
    this.cabinApplicationService = cabinApplicationService;
  }

  @GetMapping
  @Operation(summary = "Listar cabañas disponibles", description = "Obtiene todas las cabañas activas disponibles para reservas")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Lista de cabañas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CabinResponse.class), examples = @ExampleObject(value = """
          [
            {
              \"id\": 1,
              \"name\": \"Cabaña del Lago\",
              \"description\": \"Hermosa cabaña con vista al lago\",
              \"capacity\": 6,
              \"bedrooms\": 3,
              \"bathrooms\": 2,
              \"basePrice\": 150.00,
              \"maxGuests\": 6,
              \"active\": true,
                        \"amenities\": "[\\\"WiFi\\\"]",
              \"location\": "{\\\"address\\\": \\\"Lago 123\\\"}",
              \"createdAt\": \"2024-01-15T10:30:00Z\",
              \"updatedAt\": \"2024-01-20T14:45:00Z\"
            }
          ]
          """))) })
  public ResponseEntity<List<CabinResponse>> getAllCabins() {
    List<CabinResponse> cabins = cabinApplicationService.getAllActiveCabins();
    return ResponseEntity.ok(cabins);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener cabaña por ID", description = "Obtiene los detalles de una cabaña específica por su ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Cabaña encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CabinResponse.class), examples = @ExampleObject(value = """
          {
            \"id\": 1,
            \"name\": \"Cabaña del Lago\",
            \"description\": \"Hermosa cabaña con vista al lago\",
            \"capacity\": 6,
            \"bedrooms\": 3,
            \"bathrooms\": 2,
            \"basePrice\": 150.00,
            \"maxGuests\": 6,
            \"active\": true,
                        \"amenities\": "[\\\"WiFi\\\"]",
            \"location\": "{\\\"address\\\": \\\"Lago 123\\\"}",
            \"createdAt\": \"2024-01-15T10:30:00Z\",
            \"updatedAt\": \"2024-01-20T14:45:00Z\"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Cabaña no encontrada")
  })
  public ResponseEntity<CabinResponse> getCabinById(
      @Parameter(description = "ID de la cabaña", example = "1") @PathVariable Long id) {
    CabinResponse cabin = cabinApplicationService.getCabinById(id);
    return ResponseEntity.ok(cabin);
  }

  @GetMapping("/search")
  @Operation(summary = "Buscar cabañas", description = "Busca cabañas por criterios específicos como capacidad, precio o nombre")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CabinResponse.class), examples = @ExampleObject(value = """
          [
            { \"id\": 2, \"name\": \"Cabaña del Bosque\", \"capacity\": 4, \"basePrice\": 120.00 }
          ]
          """)))
  })
  public ResponseEntity<List<CabinResponse>> searchCabins(
      @Parameter(description = "Capacidad mínima requerida", example = "4") @RequestParam(required = false) Integer minCapacity,

      @Parameter(description = "Precio mínimo", example = "100.00") @RequestParam(required = false) BigDecimal minPrice,

      @Parameter(description = "Precio máximo", example = "300.00") @RequestParam(required = false) BigDecimal maxPrice,

      @Parameter(description = "Nombre de la cabaña (búsqueda parcial)", example = "lago") @RequestParam(required = false) String name) {

    List<CabinResponse> cabins = cabinApplicationService.searchCabins(minCapacity, minPrice, maxPrice, name);
    return ResponseEntity.ok(cabins);
  }
}