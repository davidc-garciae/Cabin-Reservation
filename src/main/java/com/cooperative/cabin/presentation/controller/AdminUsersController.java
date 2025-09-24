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
              "fullName": "Juan Pérez",
              "role": "USER",
              "active": true
            }
            """))), responses = {
            @ApiResponse(responseCode = "200", description = "Usuario creado o actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserResponse.class), examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "email": "usuario@ejemplo.com",
                      "fullName": "Juan Pérez",
                      "role": "USER",
                      "active": true,
                      "createdAt": "2024-01-01T10:00:00Z",
                      "updatedAt": "2024-01-01T10:00:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Email is required",
                      "path": "/api/admin/users/1"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/users/1"
                    }
                    """)))
    })
    public ResponseEntity<AdminUserResponse> upsert(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable("id") Long id,
            @RequestBody AdminUserRequest request) {
        AdminUserResponse response = adminUserApplicationService.upsertUser(
                id, request.getEmail(), request.getFullName(), request.getRole(), request.isActive());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene una lista de todos los usuarios registrados en el sistema", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AdminUserResponse.class)), examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "email": "admin@ejemplo.com",
                        "fullName": "Administrador",
                        "role": "ADMIN",
                        "active": true,
                        "createdAt": "2024-01-01T10:00:00Z",
                        "updatedAt": "2024-01-01T10:00:00Z"
                      },
                      {
                        "id": 2,
                        "email": "usuario@ejemplo.com",
                        "fullName": "Juan Pérez",
                        "role": "USER",
                        "active": true,
                        "createdAt": "2024-01-01T11:00:00Z",
                        "updatedAt": "2024-01-01T11:00:00Z"
                      }
                    ]
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/users"
                    }
                    """)))
    })
    public ResponseEntity<List<AdminUserResponse>> list() {
        return ResponseEntity.ok(adminUserApplicationService.listUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los detalles de un usuario específico por su ID", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserResponse.class), examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "email": "usuario@ejemplo.com",
                      "fullName": "Juan Pérez",
                      "role": "USER",
                      "active": true,
                      "createdAt": "2024-01-01T10:00:00Z",
                      "updatedAt": "2024-01-01T10:00:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "User not found with id: 1",
                      "path": "/api/admin/users/1"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/users/1"
                    }
                    """)))
    })
    public ResponseEntity<AdminUserResponse> get(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable("id") Long id) {
        return ResponseEntity.ok(adminUserApplicationService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por su ID", responses = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "User not found with id: 1",
                      "path": "/api/admin/users/1"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/users/1"
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar - Usuario tiene reservas activas", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Cannot delete user with active reservations",
                      "path": "/api/admin/users/1"
                    }
                    """)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del usuario a eliminar", example = "1") @PathVariable("id") Long id) {
        adminUserApplicationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
