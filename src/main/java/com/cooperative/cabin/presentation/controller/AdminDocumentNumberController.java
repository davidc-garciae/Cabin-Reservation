package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.AdminDocumentNumberApplicationService;
import com.cooperative.cabin.presentation.dto.CreateDocumentNumberRequest;
import com.cooperative.cabin.presentation.dto.DocumentNumberResponse;
import com.cooperative.cabin.presentation.dto.UpdateDocumentNumberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/documents")
@Tag(name = "👑 Admin - Documentos", description = "Gestión administrativa de números de documento")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDocumentNumberController {

    @Autowired
    private AdminDocumentNumberApplicationService documentNumberService;

    @GetMapping
    @Operation(summary = "Listar todos los documentos", description = "Retorna lista completa de números de documento en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de documentos obtenida exitosamente")
    public ResponseEntity<List<DocumentNumberResponse>> getAllDocuments() {
        List<DocumentNumberResponse> documents = documentNumberService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/paged")
    @Operation(summary = "Listar documentos paginados", description = "Retorna lista paginada de números de documento")
    @ApiResponse(responseCode = "200", description = "Lista paginada de documentos obtenida exitosamente")
    public ResponseEntity<Page<DocumentNumberResponse>> getAllDocumentsPaged(
            @Parameter(description = "Número de página (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentNumberResponse> documents = documentNumberService.getAllDocumentsPaged(pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/active")
    @Operation(summary = "Listar documentos activos", description = "Retorna lista de números de documento con estado ACTIVE")
    @ApiResponse(responseCode = "200", description = "Lista de documentos activos obtenida exitosamente")
    public ResponseEntity<List<DocumentNumberResponse>> getActiveDocuments() {
        List<DocumentNumberResponse> documents = documentNumberService.getActiveDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/disabled")
    @Operation(summary = "Listar documentos deshabilitados", description = "Retorna lista de números de documento con estado DISABLED")
    @ApiResponse(responseCode = "200", description = "Lista de documentos deshabilitados obtenida exitosamente")
    public ResponseEntity<List<DocumentNumberResponse>> getDisabledDocuments() {
        List<DocumentNumberResponse> documents = documentNumberService.getDisabledDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener documento por ID", description = "Retorna detalles de un documento específico")
    @ApiResponse(responseCode = "200", description = "Documento obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Documento no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "timestamp": "2024-01-01T10:00:00.000+00:00",
              "status": 404,
              "error": "Not Found",
              "message": "Documento no encontrado con ID: 999",
              "path": "/api/admin/documents/999"
            }
            """)))
    public ResponseEntity<DocumentNumberResponse> getDocumentById(
            @Parameter(description = "ID del documento", example = "1") @PathVariable Long id) {
        DocumentNumberResponse document = documentNumberService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/number/{documentNumber}")
    @Operation(summary = "Obtener documento por número", description = "Retorna detalles de un documento por su número")
    @ApiResponse(responseCode = "200", description = "Documento obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Documento no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "timestamp": "2024-01-01T10:00:00.000+00:00",
              "status": 404,
              "error": "Not Found",
              "message": "Documento no encontrado: 00000000",
              "path": "/api/admin/documents/number/00000000"
            }
            """)))
    public ResponseEntity<DocumentNumberResponse> getDocumentByNumber(
            @Parameter(description = "Número del documento", example = "12345678") @PathVariable String documentNumber) {
        DocumentNumberResponse document = documentNumberService.getDocumentByNumber(documentNumber);
        return ResponseEntity.ok(document);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo documento", description = "Crea un nuevo número de documento en el sistema")
    @ApiResponse(responseCode = "201", description = "Documento creado exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "id": 7,
              "documentNumber": "99999999",
              "status": "ACTIVE",
              "createdAt": "2024-01-01T10:00:00.000+00:00",
              "updatedAt": "2024-01-01T10:00:00.000+00:00"
            }
            """)))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "timestamp": "2024-01-01T10:00:00.000+00:00",
              "status": 400,
              "error": "Bad Request",
              "message": "Ya existe un documento con el número: 12345678",
              "path": "/api/admin/documents"
            }
            """)))
    public ResponseEntity<DocumentNumberResponse> createDocument(
            @Valid @RequestBody CreateDocumentNumberRequest request) {
        DocumentNumberResponse createdDocument = documentNumberService.createDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar documento", description = "Actualiza el estado de un documento existente")
    @ApiResponse(responseCode = "200", description = "Documento actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    public ResponseEntity<DocumentNumberResponse> updateDocument(
            @Parameter(description = "ID del documento", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentNumberRequest request) {
        DocumentNumberResponse updatedDocument = documentNumberService.updateDocument(id, request);
        return ResponseEntity.ok(updatedDocument);
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activar documento", description = "Activa un documento cambiando su estado a ACTIVE")
    @ApiResponse(responseCode = "200", description = "Documento activado exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "message": "Documento activado exitosamente"
            }
            """)))
    public ResponseEntity<Map<String, String>> activateDocument(
            @Parameter(description = "ID del documento", example = "1") @PathVariable Long id) {
        documentNumberService.activateDocument(id);
        return ResponseEntity.ok(Map.of("message", "Documento activado exitosamente"));
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deshabilitar documento", description = "Deshabilita un documento cambiando su estado a DISABLED")
    @ApiResponse(responseCode = "200", description = "Documento deshabilitado exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "message": "Documento deshabilitado exitosamente"
            }
            """)))
    public ResponseEntity<Map<String, String>> deactivateDocument(
            @Parameter(description = "ID del documento", example = "1") @PathVariable Long id) {
        documentNumberService.deactivateDocument(id);
        return ResponseEntity.ok(Map.of("message", "Documento deshabilitado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar documento", description = "Elimina permanentemente un documento del sistema")
    @ApiResponse(responseCode = "200", description = "Documento eliminado exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "message": "Documento eliminado exitosamente"
            }
            """)))
    @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    public ResponseEntity<Map<String, String>> deleteDocument(
            @Parameter(description = "ID del documento", example = "1") @PathVariable Long id) {
        documentNumberService.deleteDocument(id);
        return ResponseEntity.ok(Map.of("message", "Documento eliminado exitosamente"));
    }
}
