package com.cooperative.cabin;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@TestConfiguration
@Profile("test")
@EnableMethodSecurity
public class MethodSecurityTestConfig {
}
