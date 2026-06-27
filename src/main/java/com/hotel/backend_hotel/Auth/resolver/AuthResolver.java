package com.hotel.backend_hotel.Auth.resolver;

import com.hotel.backend_hotel.Auth.dto.LoginRequest;
import com.hotel.backend_hotel.Auth.dto.LoginResponse;
import com.hotel.backend_hotel.Auth.dto.RegisterRequest;
import com.hotel.backend_hotel.Auth.service.AuthService;
import com.hotel.backend_hotel.Security.JwtUtils;
import com.hotel.backend_hotel.Tokens.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthResolver {
    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtils jwtUtils;


    @MutationMapping
    public LoginResponse login(@Argument LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
    @MutationMapping
    public LoginResponse register(@Argument RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @QueryMapping
    public com.hotel.backend_hotel.Auth.entity.Usuario me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        // Si tu Usuario implementa UserDetails, el principal ES el Usuario
        if (auth.getPrincipal() instanceof com.hotel.backend_hotel.Auth.entity.Usuario usuario) {
            return usuario;
        }
        return null;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean logout(@Argument String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Debe proporcionar el token para cerrar sesión");
        }

        // Limpiar el "Bearer " si viene con prefijo
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        // 🔥 Ahora el servicio hace TODO: hash → PostgreSQL → Caffeine
        tokenBlacklistService.revocarToken(cleanToken);

        // Limpia el contexto de seguridad de la petición actual
        SecurityContextHolder.clearContext();
        return true;
    }

}
