package com.cooperative.cabin.presentation.mapper;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.presentation.dto.CreateReservationRequest;
import com.cooperative.cabin.presentation.dto.ReservationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cabin.id", target = "cabinId")
    @Mapping(source = "startDate", target = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "endDate", target = "endDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "numberOfGuests", target = "guests")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "checkInTime", target = "checkInTime", dateFormat = "HH:mm")
    @Mapping(source = "checkOutTime", target = "checkOutTime", dateFormat = "HH:mm")
    ReservationResponse toResponse(Reservation r);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "cabinId", target = "cabin.id")
    @Mapping(source = "startDate", target = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "endDate", target = "endDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "guests", target = "numberOfGuests")
    @Mapping(source = "checkInTime", target = "checkInTime", dateFormat = "HH:mm")
    @Mapping(source = "checkOutTime", target = "checkOutTime", dateFormat = "HH:mm")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "basePrice", constant = "100.00")
    @Mapping(target = "finalPrice", constant = "100.00")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    Reservation fromCreateRequest(CreateReservationRequest req);
}
