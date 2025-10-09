package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.DocumentNumber;
import com.cooperative.cabin.infrastructure.repository.DocumentNumberJpaRepository;
import com.cooperative.cabin.presentation.dto.CreateDocumentNumberRequest;
import com.cooperative.cabin.presentation.dto.DocumentNumberResponse;
import com.cooperative.cabin.presentation.dto.UpdateDocumentNumberRequest;
import com.cooperative.cabin.presentation.mapper.DocumentNumberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminDocumentNumberApplicationService {

    @Autowired
    private DocumentNumberJpaRepository documentNumberRepository;

    @Autowired
    private DocumentNumberMapper documentNumberMapper;

    @Transactional(readOnly = true)
    public List<DocumentNumberResponse> getAllDocuments() {
        List<DocumentNumber> documents = documentNumberRepository.findAll();
        return documents.stream()
                .map(documentNumberMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<DocumentNumberResponse> getAllDocumentsPaged(Pageable pageable) {
        Page<DocumentNumber> documents = documentNumberRepository.findAll(pageable);
        return documents.map(documentNumberMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DocumentNumberResponse getDocumentById(Long id) {
        DocumentNumber document = documentNumberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));
        return documentNumberMapper.toResponse(document);
    }

    @Transactional(readOnly = true)
    public DocumentNumberResponse getDocumentByNumber(String documentNumber) {
        DocumentNumber document = documentNumberRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + documentNumber));
        return documentNumberMapper.toResponse(document);
    }

    public DocumentNumberResponse createDocument(CreateDocumentNumberRequest request) {
        // Verificar si el documento ya existe
        if (documentNumberRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new IllegalArgumentException("Ya existe un documento con el número: " + request.documentNumber());
        }

        DocumentNumber document = documentNumberMapper.fromCreateRequest(request);
        DocumentNumber savedDocument = documentNumberRepository.save(document);
        return documentNumberMapper.toResponse(savedDocument);
    }

    public DocumentNumberResponse updateDocument(Long id, UpdateDocumentNumberRequest request) {
        DocumentNumber existingDocument = documentNumberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));

        DocumentNumber updatedDocument = documentNumberMapper.fromUpdateRequest(request);
        updatedDocument.setId(existingDocument.getId());
        updatedDocument.setDocumentNumber(existingDocument.getDocumentNumber());
        updatedDocument.setCreatedAt(existingDocument.getCreatedAt());

        DocumentNumber savedDocument = documentNumberRepository.save(updatedDocument);
        return documentNumberMapper.toResponse(savedDocument);
    }

    public void deleteDocument(Long id) {
        if (!documentNumberRepository.existsById(id)) {
            throw new IllegalArgumentException("Documento no encontrado con ID: " + id);
        }
        documentNumberRepository.deleteById(id);
    }

    public void activateDocument(Long id) {
        DocumentNumber document = documentNumberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));

        document.setStatus(DocumentNumber.DocumentStatus.ACTIVE);
        documentNumberRepository.save(document);
    }

    public void deactivateDocument(Long id) {
        DocumentNumber document = documentNumberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));

        document.setStatus(DocumentNumber.DocumentStatus.DISABLED);
        documentNumberRepository.save(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentNumberResponse> getActiveDocuments() {
        List<DocumentNumber> activeDocuments = documentNumberRepository
                .findByStatus(DocumentNumber.DocumentStatus.ACTIVE);
        return activeDocuments.stream()
                .map(documentNumberMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentNumberResponse> getDisabledDocuments() {
        List<DocumentNumber> disabledDocuments = documentNumberRepository
                .findByStatus(DocumentNumber.DocumentStatus.DISABLED);
        return disabledDocuments.stream()
                .map(documentNumberMapper::toResponse)
                .toList();
    }
}
