package com.cooperative.cabin.application.service;

import java.util.List;
import java.util.Map;

public interface AvailabilityApplicationService {
    List<String> getAvailableDates();

    Map<String, Boolean> getAvailabilityCalendar(int year, int month);
}
