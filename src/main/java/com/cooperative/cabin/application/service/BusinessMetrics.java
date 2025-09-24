package com.cooperative.cabin.application.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

public class BusinessMetrics {

    private final MeterRegistry meterRegistry;

    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementReservationCreated() {
        if (meterRegistry == null)
            return;
        meterRegistry.counter("reservations.created").increment();
    }

    public void incrementReservationCancelled() {
        if (meterRegistry == null)
            return;
        meterRegistry.counter("reservations.cancelled").increment();
    }

    public void incrementStatusTransition(String from, String to) {
        if (meterRegistry == null)
            return;
        meterRegistry.counter("reservations.status.transition", Tags.of(Tag.of("from", from), Tag.of("to", to)))
                .increment();
    }

    public void incrementSchedulerTransition(String type) {
        if (meterRegistry == null)
            return;
        meterRegistry.counter("scheduler.reservations.transition", Tags.of(Tag.of("type", type))).increment();
    }
}


