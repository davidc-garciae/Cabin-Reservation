package com.cooperative.cabin;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.AuditingHandler;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestMvcConfiguration {

    @Bean
    @Primary
    public AuditingHandler auditingHandler() {
        return mock(AuditingHandler.class);
    }
}