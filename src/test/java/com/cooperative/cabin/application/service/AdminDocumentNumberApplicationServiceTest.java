package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.DocumentNumber;
import com.cooperative.cabin.infrastructure.repository.DocumentNumberJpaRepository;
import com.cooperative.cabin.presentation.dto.CreateDocumentNumberRequest;
import com.cooperative.cabin.presentation.dto.UpdateDocumentNumberRequest;
import com.cooperative.cabin.presentation.mapper.DocumentNumberMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminDocumentNumberApplicationServiceTest {

    private DocumentNumberJpaRepository repository;
    private AdminDocumentNumberApplicationService service;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(DocumentNumberJpaRepository.class);
        service = new AdminDocumentNumberApplicationService();
        ReflectionTestUtils.setField(service, "documentNumberRepository", repository);
        ReflectionTestUtils.setField(service, "documentNumberMapper", DocumentNumberMapper.INSTANCE);
    }

    @Test
    void shouldCreateDocument() {
        CreateDocumentNumberRequest req = new CreateDocumentNumberRequest("ABC", "ACTIVE");
        when(repository.existsByDocumentNumber("ABC")).thenReturn(false);
        when(repository.save(any(DocumentNumber.class))).thenAnswer(inv -> {
            DocumentNumber dn = inv.getArgument(0);
            dn.setId(1L);
            return dn;
        });

        var resp = service.createDocument(req);

        assertThat(resp.id()).isEqualTo(1L);
        verify(repository).save(any(DocumentNumber.class));
    }

    @Test
    void shouldFailOnDuplicateCreate() {
        when(repository.existsByDocumentNumber("ABC")).thenReturn(true);

        assertThatThrownBy(() -> service.createDocument(new CreateDocumentNumberRequest("ABC", "ACTIVE")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldUpdatePreservingImmutableFields() {
        DocumentNumber existing = new DocumentNumber("ABC", DocumentNumber.DocumentStatus.ACTIVE);
        existing.setId(10L);
        existing.setCreatedAt(java.time.LocalDateTime.now().minusDays(1));
        when(repository.findById(10L)).thenReturn(Optional.of(existing));
        when(repository.save(any(DocumentNumber.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = service.updateDocument(10L, new UpdateDocumentNumberRequest("DISABLED"));

        assertThat(resp.id()).isEqualTo(10L);
        assertThat(resp.status()).isEqualTo("DISABLED");
        verify(repository).save(any(DocumentNumber.class));
    }

    @Test
    void shouldActivateAndDeactivate() {
        DocumentNumber dn = new DocumentNumber("ABC", DocumentNumber.DocumentStatus.DISABLED);
        when(repository.findById(1L)).thenReturn(Optional.of(dn));

        service.activateDocument(1L);
        assertThat(dn.getStatus()).isEqualTo(DocumentNumber.DocumentStatus.ACTIVE);

        when(repository.findById(1L)).thenReturn(Optional.of(dn));
        service.deactivateDocument(1L);
        assertThat(dn.getStatus()).isEqualTo(DocumentNumber.DocumentStatus.DISABLED);
    }
}

