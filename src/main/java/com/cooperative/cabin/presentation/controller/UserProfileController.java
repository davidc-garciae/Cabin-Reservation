package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.AdminUserApplicationService;
import com.cooperative.cabin.application.service.AuthApplicationService;
import com.cooperative.cabin.presentation.dto.AdminUserResponse;
import com.cooperative.cabin.presentation.dto.ChangePasswordRequest;
import com.cooperative.cabin.presentation.dto.ChangePasswordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "Perfil del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

  private final AdminUserApplicationService adminUserApplicationService;
  private final AuthApplicationService authApplicationService;

  public UserProfileController(AdminUserApplicationService adminUserApplicationService,
      AuthApplicationService authApplicationService) {
    this.adminUserApplicationService = adminUserApplicationService;
    this.authApplicationService = authApplicationService;
  }

  @GetMapping("/profile")
  @Operation(summary = "Obtener perfil del usuario", description = "Obtiene los datos del perfil del usuario autenticado", responses = {
      @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserResponse.class), examples = @ExampleObject(value = """
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
          """))),
      @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Full authentication is required to access this resource",
            "path": "/api/users/profile"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 404,
            "error": "Not Found",
            "message": "User not found with id: 1",
            "path": "/api/users/profile"
          }
          """)))
  })
  public ResponseEntity<AdminUserResponse> get(
      @Parameter(description = "ID del usuario autenticado", example = "1") @RequestHeader("X-User-Id") Long userId) {
    return ResponseEntity.ok(adminUserApplicationService.getProfile(userId));
  }

  @Schema(description = "Solicitud de actualización de perfil")
  public static class UpdateUserProfileRequest {
    @Schema(description = "Nuevo email del usuario", example = "nuevo@ejemplo.com")
    private String email;

    @Schema(description = "Nuevo número de documento del usuario", example = "87654321")
    private String documentNumber;

    @Schema(description = "Nuevo nombre completo del usuario", example = "Juan Carlos Pérez")
    private String fullName;

    public String getEmail() {
      return email;
    }

    public String getDocumentNumber() {
      return documentNumber;
    }

    public String getFullName() {
      return fullName;
    }
  }

  @PutMapping("/profile")
  @Operation(summary = "Actualizar perfil del usuario", description = "Actualiza los datos del perfil del usuario autenticado", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del perfil a actualizar", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateUserProfileRequest.class), examples = @ExampleObject(value = """
      {
        "email": "nuevo@ejemplo.com",
        "documentNumber": "87654321",
        "fullName": "Juan Carlos Pérez"
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserResponse.class), examples = @ExampleObject(value = """
          {
            "id": 1,
            "email": "nuevo@ejemplo.com",
            "documentNumber": "87654321",
            "fullName": "Juan Carlos Pérez",
            "role": "USER",
            "active": true,
            "createdAt": "2024-01-01T10:00:00Z",
            "updatedAt": "2024-01-01T11:00:00Z"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Email format is invalid",
            "path": "/api/users/profile"
          }
          """))),
      @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Full authentication is required to access this resource",
            "path": "/api/users/profile"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 404,
            "error": "Not Found",
            "message": "User not found with id: 1",
            "path": "/api/users/profile"
          }
          """)))
  })
  public ResponseEntity<AdminUserResponse> put(
      @Parameter(description = "ID del usuario autenticado", example = "1") @RequestHeader("X-User-Id") Long userId,
      @RequestBody UpdateUserProfileRequest request) {
    return ResponseEntity
        .ok(adminUserApplicationService.updateProfile(userId, request.getEmail(),
            request.getDocumentNumber(), request.getFullName()));
  }

  @PutMapping("/change-password")
  @Operation(summary = "Cambiar contraseña", description = "Permite al usuario autenticado cambiar su contraseña o PIN. Para administradores requiere contraseña de mínimo 6 caracteres. Para usuarios normales puede ser PIN de 4 dígitos o contraseña de 6-50 caracteres.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para cambio de contraseña", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangePasswordRequest.class), examples = @ExampleObject(value = """
      {
        "currentPassword": "1234",
        "newPassword": "SecurePass123"
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangePasswordResponse.class), examples = @ExampleObject(value = """
          {
            "message": "Contraseña actualizada exitosamente",
            "mustChangePassword": false
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Contraseña actual incorrecta",
            "path": "/api/users/change-password"
          }
          """))),
      @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Full authentication is required to access this resource",
            "path": "/api/users/change-password"
          }
          """)))
  })
  public ResponseEntity<ChangePasswordResponse> changePassword(
      @Parameter(description = "ID del usuario autenticado", example = "1") @RequestHeader("X-User-Id") Long userId,
      @Valid @RequestBody ChangePasswordRequest request) {
    Map<String, Object> result = authApplicationService.changePassword(userId, request.getCurrentPassword(),
        request.getNewPassword());
    ChangePasswordResponse response = new ChangePasswordResponse(
        (String) result.get("message"),
        (Boolean) result.get("mustChangePassword"));
    return ResponseEntity.ok(response);
  }
}
