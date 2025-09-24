package com.cooperative.cabin.infrastructure.config;

import com.cooperative.cabin.infrastructure.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Público
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/webjars/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // Cabañas públicas (para que los clientes puedan ver las cabañas sin login)
                        .requestMatchers("/api/cabins").permitAll()
                        .requestMatchers("/api/cabins/{id}").permitAll()
                        .requestMatchers("/api/cabins/search").permitAll()
                        // Disponibilidad pública (para que los clientes puedan ver fechas disponibles
                        // sin login)
                        .requestMatchers("/api/availability").permitAll()
                        .requestMatchers("/api/availability/calendar").permitAll()
                        .requestMatchers("/api/availability/calendar/list").permitAll()
                        // Admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Usuario autenticado
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/reservations/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
