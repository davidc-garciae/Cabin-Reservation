package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.AuthApplicationService;
import com.cooperative.cabin.presentation.dto.DocumentLoginRequest;
import com.cooperative.cabin.presentation.dto.RecoverPasswordRequest;
import com.cooperative.cabin.presentation.dto.RegisterRequest;
import com.cooperative.cabin.presentation.dto.ResetPasswordRequest;
import com.cooperative.cabin.presentation.dto.ValidateTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticación JWT")
public class AuthController {

  private final AuthApplicationService authApplicationService;

  public AuthController(AuthApplicationService authApplicationService) {
    this.authApplicationService = authApplicationService;
  }

  @PostMapping("/login")
  @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con número de documento y devuelve tokens JWT de acceso y refresh", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Credenciales de usuario con número de documento", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentLoginRequest.class), examples = @ExampleObject(value = """
      {
        "documentNumber": "12345678",
        "password": "password123"
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Autenticación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(example = """
          {
            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
          }
          """))),
      @ApiResponse(responseCode = "401", description = "Credenciales inválidas o número de documento deshabilitado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Número de documento no válido o deshabilitado",
            "path": "/api/auth/login"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Número de documento y contraseña son obligatorios",
            "path": "/api/auth/login"
          }
          """)))
  })
  public ResponseEntity<Map<String, String>> login(@Valid @RequestBody DocumentLoginRequest request) {
    Map<String, String> tokens = authApplicationService.login(request.getDocumentNumber(), request.getPassword());
    return ResponseEntity.ok(tokens);
  }

  @PostMapping("/refresh")
  @Operation(summary = "Renovar token de acceso", description = "Genera un nuevo token de acceso usando un token de refresh válido", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Token de refresh", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefreshRequest.class), examples = @ExampleObject(value = """
      {
        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Token renovado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(example = """
          {
            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
          }
          """))),
      @ApiResponse(responseCode = "401", description = "Token de refresh inválido o expirado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Invalid or expired refresh token",
            "path": "/api/auth/refresh"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Refresh token is required",
            "path": "/api/auth/refresh"
          }
          """)))
  })
  public ResponseEntity<Map<String, String>> refresh(@RequestBody RefreshRequest request) {
    Map<String, String> tokens = authApplicationService.refreshToken(request.getRefreshToken());
    return ResponseEntity.ok(tokens);
  }

  @Schema(description = "Solicitud de renovación de token")
  public static class RefreshRequest {
    @Schema(description = "Token de refresh", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    public String getRefreshToken() {
      return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
    }
  }

  @PostMapping("/recover-password")
  @Operation(summary = "Recuperar contraseña", description = "Envía un token de recuperación por email al usuario usando su número de documento", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Número de documento del usuario", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecoverPasswordRequest.class), examples = @ExampleObject(value = """
      {
        "documentNumber": "12345678"
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Solicitud procesada (siempre devuelve 200 por seguridad)"),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Document number format is invalid",
            "path": "/api/auth/recover-password"
          }
          """)))
  })
  public ResponseEntity<Void> recoverPassword(@Valid @RequestBody RecoverPasswordRequest request) {
    authApplicationService.recoverPassword(request.documentNumber());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Restablecer contraseña", description = "Restablece la contraseña usando un token de recuperación válido", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Token y nuevo PIN", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResetPasswordRequest.class), examples = @ExampleObject(value = """
      {
        "token": "abc123def456",
        "newPin": "1234"
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente"),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Token inválido o expirado",
            "path": "/api/auth/reset-password"
          }
          """)))
  })
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    authApplicationService.resetPassword(request.token(), request.newPin());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/validate-token")
  @Operation(summary = "Validar token", description = "Valida si un token JWT es válido y no ha expirado", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Token a validar", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidateTokenRequest.class), examples = @ExampleObject(value = """
      {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Token válido", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "valid": true
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Token is required",
            "path": "/api/auth/validate-token"
          }
          """)))
  })
  public ResponseEntity<Map<String, Boolean>> validateToken(@Valid @RequestBody ValidateTokenRequest request) {
    boolean isValid = authApplicationService.validateToken(request.token());
    return ResponseEntity.ok(Map.of("valid", isValid));
  }

  @PostMapping("/register")
  @Operation(summary = "Registro de nuevo usuario", description = """
      Registra un nuevo usuario validando su número de documento contra la base de datos de asociados activos.

      **Validaciones:**
      - Documento debe existir en la base de datos de asociados activos
      - Email debe ser único
      - Documento no puede estar ya registrado
      - PIN debe tener exactamente 4 dígitos
      - Rol debe ser PROFESSOR o RETIREE (no se permite ADMIN)
      """, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de registro del usuario", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterRequest.class), examples = @ExampleObject(value = """
      {
        "documentNumber": "99999999",
        "email": "nuevo.usuario@email.com",
        "name": "Nuevo Usuario",
        "phone": "+57-300-999-9999",
        "pin": "1234",
        "role": "PROFESSOR"
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Registro exitoso", content = @Content(mediaType = "application/json", schema = @Schema(example = """
          {
            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Ya existe un usuario con este número de documento",
            "path": "/api/auth/register"
          }
          """))),
      @ApiResponse(responseCode = "409", description = "Conflicto - Usuario ya existe", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 409,
            "error": "Conflict",
            "message": "Número de documento no válido o deshabilitado",
            "path": "/api/auth/register"
          }
          """)))
  })
  public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
    Map<String, String> tokens = authApplicationService.register(
        request.getDocumentNumber(),
        request.getEmail(),
        request.getName(),
        request.getPhone(),
        request.getPin(),
        request.getRole());
    return ResponseEntity.ok(tokens);
  }
}
