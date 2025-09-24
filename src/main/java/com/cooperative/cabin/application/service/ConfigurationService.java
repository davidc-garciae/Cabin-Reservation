package com.cooperative.cabin.application.service;

import java.time.Duration;
import java.util.Map;

public interface ConfigurationService {
    String getString(String key);

    int getInt(String key);

    Duration getDurationMinutes(String key);

    Map<String, String> getAll();

    void setValue(String key, String value);
}

