package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public class AvailabilityPolicies {

    public static boolean respectsMandatoryBlockRanges(LocalDate requestStart, LocalDate requestEnd, Long cabinId,
            List<AvailabilityBlock> blocks) {
        // La reserva debe coincidir exactamente con el rango cuando cae dentro de un
        // bloqueo
        for (AvailabilityBlock block : blocks) {
            if (!block.getCabinId().equals(cabinId))
                continue;
            LocalDate bStart = block.getStartDate();
            LocalDate bEnd = block.getEndDate();
            boolean overlaps = !(requestEnd.isBefore(bStart) || requestStart.isAfter(bEnd));
            if (overlaps) {
                // Si se solapa, solo se permite si coincide exactamente con el rango bloqueado
                // completo
                boolean exact = requestStart.equals(bStart) && requestEnd.equals(bEnd);
                if (!exact)
                    return false;
            }
        }
        return true;
    }
}
