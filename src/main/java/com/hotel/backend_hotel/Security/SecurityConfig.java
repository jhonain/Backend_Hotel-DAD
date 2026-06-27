package com.hotel.backend_hotel.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/graphql", "/graphiql/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 🆕 ESTO ES LO QUE FALTABA
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(graphqlEntryPoint())
                        .accessDeniedHandler(graphqlAccessDeniedHandler())
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint graphqlEntryPoint() {
        return (request, response, authException) -> {
            if ("/graphql".equals(request.getRequestURI())) {
                writeGraphQLError(response, authException.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }

    @Bean
    public AccessDeniedHandler graphqlAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            if ("/graphql".equals(request.getRequestURI())) {
                writeGraphQLError(response, accessDeniedException.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        };
    }

    private void writeGraphQLError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK); // GraphQL siempre 200
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"errors\":[{\"message\":\"" + message + "\"}]}"
        );
    }
}