package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.presentation.dto.AdminMetricsResponse;
import org.springframework.stereotype.Service;

@Service
public class AdminMetricsService {

    public AdminMetricsResponse getSummary() {
        // Implementación mínima; los tests mockean este servicio
        return new AdminMetricsResponse();
    }
}
