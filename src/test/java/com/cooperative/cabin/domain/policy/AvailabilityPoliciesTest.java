package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityPoliciesTest {

        @Test
        void respectsMandatoryBlockRanges_false_whenPartialInsideBlock() {
                Long cabinId = 1L;
                Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
                User admin = TestEntityFactory.createAdmin(1L);
                AvailabilityBlock block = TestEntityFactory.createAvailabilityBlock(cabin, LocalDate.of(2025, 4, 1),
                                LocalDate.of(2025, 4, 4), "Test block", admin);
                block.setId(1L);
                List<AvailabilityBlock> blocks = List.of(block);
                boolean ok = AvailabilityPolicies.respectsMandatoryBlockRanges(
                                LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2), cabinId, blocks);
                assertFalse(ok);
        }

        @Test
        void respectsMandatoryBlockRanges_true_whenExactMatch() {
                Long cabinId = 1L;
                Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
                User admin = TestEntityFactory.createAdmin(1L);
                AvailabilityBlock block = TestEntityFactory.createAvailabilityBlock(cabin, LocalDate.of(2025, 4, 1),
                                LocalDate.of(2025, 4, 4), "Test block", admin);
                block.setId(1L);
                List<AvailabilityBlock> blocks = List.of(block);
                boolean ok = AvailabilityPolicies.respectsMandatoryBlockRanges(
                                LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 4), cabinId, blocks);
                assertTrue(ok);
        }

        @Test
        void respectsMandatoryBlockRanges_true_whenOutsideBlock() {
                Long cabinId = 1L;
                Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
                User admin = TestEntityFactory.createAdmin(1L);
                AvailabilityBlock block = TestEntityFactory.createAvailabilityBlock(cabin, LocalDate.of(2025, 4, 1),
                                LocalDate.of(2025, 4, 4), "Test block", admin);
                block.setId(1L);
                List<AvailabilityBlock> blocks = List.of(block);
                boolean ok = AvailabilityPolicies.respectsMandatoryBlockRanges(
                                LocalDate.of(2025, 4, 10), LocalDate.of(2025, 4, 12), cabinId, blocks);
                assertTrue(ok);
        }
}
