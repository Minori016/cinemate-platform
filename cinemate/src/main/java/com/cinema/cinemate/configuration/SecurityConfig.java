package com.cinema.cinemate.configuration;

import com.cinema.cinemate.enums.UserEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final CorsConfigurationSource corsConfigurationSource;
        private final JwtDecoder jwtDecoder;
        private final JwtAuthenticationConverter jwtAuthenticationConverter;

        private final String[] PUBLIC_ENDPOINTS = {
                        "/api/v1/auth/**"
        };

        public SecurityConfig(CorsConfigurationSource corsConfigurationSource,
                        JwtDecoder jwtDecoder,
                        JwtAuthenticationConverter jwtAuthenticationConverter) {
                this.corsConfigurationSource = corsConfigurationSource;
                this.jwtDecoder = jwtDecoder;
                this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(request -> request
                                                // Public endpoints
                                                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/movies", "/api/v1/movies/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/genres", "/api/v1/genres/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/countries", "/api/v1/countries/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/cinema-rooms", "/api/v1/cinema-rooms/**").permitAll()

                                                // Swagger / OpenAPI
                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // Authenticated user self info
                                                .requestMatchers("/api/v1/users/myinfo", "/api/v1/users/myinfo/**").authenticated()

                                                // Admin only endpoints (all under /api/v1/admin/**)
                                                .requestMatchers("/api/v1/admin/**").hasAnyRole(UserEnum.ADMIN.name(), UserEnum.MANAGER.name())

                                                // All other requests need authentication
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .decoder(jwtDecoder)
                                                                .jwtAuthenticationConverter(
                                                                                jwtAuthenticationConverter)));

                return http.build();
        }
}
