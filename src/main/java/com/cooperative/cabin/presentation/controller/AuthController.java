package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.AuthApplicationService;
import com.cooperative.cabin.presentation.dto.RecoverPasswordRequest;
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
  @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens JWT de acceso y refresh", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Credenciales de usuario", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequest.class), examples = @ExampleObject(value = """
      {
        "username": "admin",
        "password": "password123"
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Autenticación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(example = """
          {
            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
          }
          """))),
      @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Invalid credentials",
            "path": "/api/auth/login"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Username and password are required",
            "path": "/api/auth/login"
          }
          """)))
  })
  public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
    // NOTE(dev-only): si el usuario es 'admin', emitimos rol ADMIN para facilitar
    // pruebas en Swagger.
    // Este atajo NO debe permanecer en producción; reemplazar por emisión de roles
    // desde la base de datos/IdP.
    Map<String, String> tokens = authApplicationService.login(request.getUsername(), request.getPassword());
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

  @Schema(description = "Solicitud de login")
  public static class LoginRequest {
    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;

    @Schema(description = "Contraseña", example = "password123")
    private String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
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
  @Operation(summary = "Recuperar contraseña", description = "Envía un token de recuperación por email al usuario", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email del usuario", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecoverPasswordRequest.class), examples = @ExampleObject(value = """
      {
        "email": "usuario@ejemplo.com"
      }
      """))), responses = {
      @ApiResponse(responseCode = "200", description = "Solicitud procesada (siempre devuelve 200 por seguridad)"),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Email format is invalid",
            "path": "/api/auth/recover-password"
          }
          """)))
  })
  public ResponseEntity<Void> recoverPassword(@Valid @RequestBody RecoverPasswordRequest request) {
    authApplicationService.recoverPassword(request.email());
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
}
