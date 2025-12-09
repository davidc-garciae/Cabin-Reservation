package com.cooperative.cabin.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentNumberTest {

    @Test
    void shouldActivateAndDisable() {
        DocumentNumber doc = new DocumentNumber("123", DocumentNumber.DocumentStatus.DISABLED);

        assertTrue(doc.isDisabled());
        assertFalse(doc.isActive());

        doc.activate();
        assertTrue(doc.isActive());
        assertFalse(doc.isDisabled());

        doc.disable();
        assertTrue(doc.isDisabled());
        assertFalse(doc.isActive());
    }
}

