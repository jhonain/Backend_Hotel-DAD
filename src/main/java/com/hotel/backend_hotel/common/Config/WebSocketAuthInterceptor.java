package com.hotel.backend_hotel.common.Config;

import com.hotel.backend_hotel.Security.JwtUtils;
import com.hotel.backend_hotel.Tokens.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements WebSocketGraphQlInterceptor {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public Mono<Object> handleConnectionInitialization(WebSocketSessionInfo sessionInfo, Map<String, Object> connectPayload) {
        String authHeader = (String) connectPayload.get("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new AccessDeniedException("Token de autorización requerido para WebSocket"));
        }

        String token = authHeader.substring(7);

        if (tokenBlacklistService.esTokenInvalido(token)) {
            return Mono.error(new AccessDeniedException("Token revocado"));
        }

        try {
            String username = jwtUtils.extractUsername(token);
            if (username == null) {
                return Mono.error(new AccessDeniedException("Token corrupto"));
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtils.isTokenValid(token, userDetails)) {
                return Mono.error(new AccessDeniedException("Token inválido o expirado"));
            }
        } catch (Exception e) {
            return Mono.error(new AccessDeniedException("Error de autenticación: " + e.getMessage()));
        }

        return Mono.just(Map.of());
    }
}
