package com.hotel.backend_hotel.Security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        System.out.println("=== NUEVA REQUEST ===");
        System.out.println("PATH = " + path);

        // Rutas públicas: dejar pasar sin validar token
        if (path.startsWith("/graphiql")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String authHeader = request.getHeader("Authorization");
            System.out.println("HEADER = " + authHeader);

            // Sin token: pasa como anónimo
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("SIN HEADER BEARER → SIGUE COMO ANONIMO");
                filterChain.doFilter(request, response);
                return;
            }

            // Extraer el token
            String jwt = authHeader.substring(7);

            // Extraer username — puede lanzar ExpiredJwtException o JwtException
            String username = jwtUtils.extractUsername(jwt);
            System.out.println("USERNAME TOKEN = " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                boolean valid = jwtUtils.isTokenValid(jwt, userDetails);

                System.out.println("USER BD = " + userDetails.getUsername());
                System.out.println("TOKEN VALID = " + valid);

                if (valid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("AUTH SET = " + SecurityContextHolder.getContext().getAuthentication());
                } else {
                    System.out.println("TOKEN INVALIDO → RESPUESTA JSON");
                    escribirError(response, "Token revocado o inválido. Inicie sesión nuevamente.");
                    return;
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            System.out.println("TOKEN EXPIRADO: " + e.getMessage());
            escribirError(response, "Token expirado. Inicie sesión nuevamente.");

        } catch (JwtException e) {
            System.out.println("TOKEN JWT INVALIDO: " + e.getMessage());
            escribirError(response, "Token inválido.");

        } catch (Exception e) {
            System.out.println("ERROR INESPERADO EN FILTRO: " + e.getMessage());
            escribirError(response, "Error de autenticación inesperado.");
        }
    }

    // Método auxiliar para no repetir código
    private void escribirError(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK); // GraphQL siempre responde 200
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"errors\":[{\"message\":\"" + mensaje + "\"}]}"
        );
    }
}