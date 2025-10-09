package com.cooperative.cabin.presentation.mapper;

import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.presentation.dto.CabinResponse;
import com.cooperative.cabin.presentation.dto.CreateCabinRequest;
import com.cooperative.cabin.presentation.dto.UpdateCabinRequest;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CabinMapper {
    public static final CabinMapper INSTANCE = new CabinMapper();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public CabinResponse toResponse(Cabin cabin) {
        if (cabin == null)
            return null;
        return new CabinResponse(
                cabin.getId(),
                cabin.getName(),
                cabin.getDescription(),
                cabin.getCapacity(),
                cabin.getBedrooms(),
                cabin.getBathrooms(),
                cabin.getBasePrice(),
                cabin.getMaxGuests(),
                cabin.getActive(),
                cabin.getAmenities(),
                cabin.getLocation(),
                cabin.getCreatedAt(),
                cabin.getUpdatedAt(),
                cabin.getDefaultCheckInTime() != null ? cabin.getDefaultCheckInTime().format(TIME_FORMATTER) : null,
                cabin.getDefaultCheckOutTime() != null ? cabin.getDefaultCheckOutTime().format(TIME_FORMATTER) : null);
    }

    public Cabin fromCreateRequest(CreateCabinRequest req) {
        if (req == null)
            return null;

        LocalTime defaultCheckInTime = req.defaultCheckInTime() != null
                ? LocalTime.parse(req.defaultCheckInTime(), TIME_FORMATTER)
                : null;
        LocalTime defaultCheckOutTime = req.defaultCheckOutTime() != null
                ? LocalTime.parse(req.defaultCheckOutTime(), TIME_FORMATTER)
                : null;

        return new Cabin(
                req.name(),
                req.description(),
                req.capacity(),
                req.bedrooms(),
                req.bathrooms(),
                req.basePrice(),
                req.maxGuests(),
                req.amenities(),
                req.location(),
                defaultCheckInTime,
                defaultCheckOutTime);
    }

    public void updateEntity(Cabin cabin, UpdateCabinRequest req) {
        if (cabin == null || req == null)
            return;
        if (req.name() != null)
            cabin.setName(req.name());
        if (req.description() != null)
            cabin.setDescription(req.description());
        if (req.capacity() != null)
            cabin.setCapacity(req.capacity());
        if (req.bedrooms() != null)
            cabin.setBedrooms(req.bedrooms());
        if (req.bathrooms() != null)
            cabin.setBathrooms(req.bathrooms());
        if (req.basePrice() != null)
            cabin.setBasePrice(req.basePrice());
        if (req.maxGuests() != null)
            cabin.setMaxGuests(req.maxGuests());
        if (req.active() != null)
            cabin.setActive(req.active());
        if (req.amenities() != null)
            cabin.setAmenities(req.amenities());
        if (req.location() != null)
            cabin.setLocation(req.location());
    }
}
