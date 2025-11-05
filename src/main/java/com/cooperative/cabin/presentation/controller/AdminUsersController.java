package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.AdminUserApplicationService;
import com.cooperative.cabin.presentation.dto.AdminUserRequest;
import com.cooperative.cabin.presentation.dto.AdminUserResponse;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin Users", description = "Gestión de usuarios (admin)")
@SecurityRequirement(name = "bearerAuth")
public class AdminUsersController {

  private final AdminUserApplicationService adminUserApplicationService;

  public AdminUsersController(AdminUserApplicationService adminUserApplicationService) {
    this.adminUserApplicationService = adminUserApplicationService;
  }

  @PutMapping("/{id}")
  @Operation(summary = "Crear o actualizar usuario", description = "Crea un nuevo usuario o actualiza uno existente de forma idempotente", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del usuario", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserRequest.class), examples = @ExampleObject(value = """
      {
        "email": "usuario@ejemplo.com",
        "documentNumber": "12345678",
        "fullName": "Juan Pérez",
        "role": "USER",
        "active": true
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Usuario creado o actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserResponse.class), examples = @ExampleObject(value = """
          {
            "id": 1,
            "email": "usuario@ejemplo.com",
            "documentNumber": "12345678",
            "fullName": "Juan Pérez",
            "role": "USER",
            "active": true,
            "createdAt": "2024-01-01T10:00:00Z",
            "updatedAt": "2024-01-01T10:00:00Z"
          }
          """)))
  })
  public ResponseEntity<AdminUserResponse> upsert(
      @Parameter(description = "ID del usuario", example = "1") @PathVariable("id") Long id,
      @RequestBody AdminUserRequest request) {
    AdminUserResponse response = adminUserApplicationService.upsertUser(
        id, request.getEmail(), request.getDocumentNumber(), request.getFullName(), request.getRole(),
        request.isActive());
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "Listar usuarios (con filtros opcionales)", description = "Lista usuarios con filtros opcionales exactos por email y número de documento. Soporta paginación con page y size.", responses = {
      @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AdminUserResponse.class)), examples = @ExampleObject(value = """
          [
            {
              "id": 1,
              "email": "admin@cooperativa.com",
              "documentNumber": "12345678",
              "fullName": "Administrador Sistema",
              "role": "ADMIN",
              "active": true
            }
          ]
          """)))
  })
  public ResponseEntity<List<AdminUserResponse>> list(
      @Parameter(description = "Email exacto para filtrar", example = "john.doe@example.com") @RequestParam(value = "email", required = false) String email,
      @Parameter(description = "Número de documento exacto para filtrar", example = "12345678") @RequestParam(value = "documentNumber", required = false) String documentNumber,
      @Parameter(description = "Número de página (0-based)", example = "0") @RequestParam(value = "page", required = false) Integer page,
      @Parameter(description = "Tamaño de página", example = "20") @RequestParam(value = "size", required = false) Integer size
  ) {
    return ResponseEntity.ok(adminUserApplicationService.listUsers(email, documentNumber, page, size));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener usuario por ID",
      responses = {
          @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserResponse.class)))
      })
  public ResponseEntity<AdminUserResponse> getById(
      @Parameter(description = "ID del usuario", example = "1") @PathVariable("id") Long id) {
    return ResponseEntity.ok(adminUserApplicationService.getById(id));
  }

  @PostMapping("/{id}/force-password-change")
  @Operation(summary = "Forzar cambio de contraseña", description = "Fuerza que un usuario cambie su contraseña en el próximo login", responses = {
      @ApiResponse(responseCode = "200", description = "Cambio de contraseña forzado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(example = """
          {
            "message": "Usuario debe cambiar su contraseña en el próximo login"
          }
          """)))
  })
  public ResponseEntity<Map<String, String>> forcePasswordChange(
      @Parameter(description = "ID del usuario", example = "1") @PathVariable("id") Long id) {
    adminUserApplicationService.forcePasswordChange(id);
    return ResponseEntity.ok(Map.of("message", "Usuario debe cambiar su contraseña en el próximo login"));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por su ID", responses = {
      @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente")
  })
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID del usuario", example = "1") @PathVariable("id") Long id) {
    adminUserApplicationService.deleteUser(id);
    return ResponseEntity.ok().build();
  }
}
