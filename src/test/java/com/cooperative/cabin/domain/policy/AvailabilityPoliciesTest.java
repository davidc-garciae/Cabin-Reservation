package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityPoliciesTest {

    @Test
    void respectsMandatoryBlockRanges_false_whenPartialInsideBlock() {
        Long cabinId = 1L;
        List<AvailabilityBlock> blocks = List
                .of(new AvailabilityBlock(1L, cabinId, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 4)));
        boolean ok = AvailabilityPolicies.respectsMandatoryBlockRanges(
                LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2), cabinId, blocks);
        assertFalse(ok);
    }

    @Test
    void respectsMandatoryBlockRanges_true_whenExactMatch() {
        Long cabinId = 1L;
        List<AvailabilityBlock> blocks = List
                .of(new AvailabilityBlock(1L, cabinId, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 4)));
        boolean ok = AvailabilityPolicies.respectsMandatoryBlockRanges(
                LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 4), cabinId, blocks);
        assertTrue(ok);
    }

    @Test
    void respectsMandatoryBlockRanges_true_whenOutsideBlock() {
        Long cabinId = 1L;
        List<AvailabilityBlock> blocks = List
                .of(new AvailabilityBlock(1L, cabinId, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 4)));
        boolean ok = AvailabilityPolicies.respectsMandatoryBlockRanges(
                LocalDate.of(2025, 4, 10), LocalDate.of(2025, 4, 12), cabinId, blocks);
        assertTrue(ok);
    }
}
