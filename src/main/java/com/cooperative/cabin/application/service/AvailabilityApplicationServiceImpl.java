package com.cooperative.cabin.application.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AvailabilityApplicationServiceImpl implements AvailabilityApplicationService {

    @Override
    public List<String> getAvailableDates() {
        LocalDate today = LocalDate.now();
        return IntStream.rangeClosed(1, 5)
                .mapToObj(i -> today.plusDays(i).toString())
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Boolean> getAvailabilityCalendar(int year, int month) {
        Map<String, Boolean> map = new LinkedHashMap<>();
        LocalDate start = LocalDate.of(year, month, 1);
        int length = start.lengthOfMonth();
        for (int d = 1; d <= length; d++) {
            LocalDate day = LocalDate.of(year, month, d);
            map.put(day.toString(), d % 2 == 0);
        }
        return map;
    }
}
