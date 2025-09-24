package com.cooperative.cabin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class CabinReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CabinReservationApplication.class, args);
	}

	@Configuration
	@EnableJpaAuditing
	@Profile("!test")
	public static class JpaAuditingConfiguration {
		// JPA auditing configuration
	}
}
