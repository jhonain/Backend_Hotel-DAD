package com.hotel.backend_hotel.common.Config;

import com.hotel.backend_hotel.Security.JwtUtils;
import com.hotel.backend_hotel.Tokens.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class GraphQLWebSocketConfig implements WebGraphQlInterceptor {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (tokenBlacklistService.esTokenInvalido(token)) {
                return Mono.error(new AccessDeniedException("Token revocado. Conexión terminada."));
            }

            try {
                String username = jwtUtils.extractUsername(token);
                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (!jwtUtils.isTokenValid(token, userDetails)) {
                        return Mono.error(new AccessDeniedException("Token inválido o expirado"));
                    }
                } else {
                    return Mono.error(new AccessDeniedException("Token corrupto"));
                }
            } catch (Exception e) {
                return Mono.error(new AccessDeniedException("Error de autenticación: " + e.getMessage()));
            }

            request.configureExecutionInput((input, builder) ->
                    builder.graphQLContext(ctx -> ctx.put("tokenRaw", token)).build()
            );
        }

        return chain.next(request);
    }
}