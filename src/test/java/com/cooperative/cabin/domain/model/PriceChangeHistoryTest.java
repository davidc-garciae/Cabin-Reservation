package com.cooperative.cabin.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PriceChangeHistoryTest {

    @Test
    void shouldExposeIdsForRelatedEntities() {
        PriceRange priceRange = new PriceRange();
        priceRange.setId(10L);
        User user = new User();
        user.setId(20L);

        PriceChangeHistory history = new PriceChangeHistory(
                priceRange,
                BigDecimal.ONE,
                BigDecimal.TEN,
                "reason",
                user
        );

        assertEquals(10L, history.getPriceRangeId());
        assertEquals(20L, history.getChangedById());
    }

    @Test
    void shouldHandleNullRelations() {
        PriceChangeHistory history = new PriceChangeHistory(null, null, null, null, null);

        assertNull(history.getPriceRangeId());
        assertNull(history.getChangedById());
    }
}

