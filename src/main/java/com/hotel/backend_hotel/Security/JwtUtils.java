package com.hotel.backend_hotel.Security;

import com.hotel.backend_hotel.Auth.entity.Usuario;
import com.hotel.backend_hotel.RolPermisos.entity.Rol;
import com.hotel.backend_hotel.Tokens.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey; // ← OBLIGATORIO para 0.12.x
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtUtils {

    private static final String SECRET_KEY = "NU74y4INSHhTnmz/ebXTP5N8d7OdO+8v0GmS55M9E1yQyHYUeEHy9uK2Slil27TzVWhwLmvjPIuJr74oRxPypw==";
    private final TokenBlacklistService tokenBlacklistService;

    // 1. Generar token
    public String generateToken(UserDetails userDetails) {

        Usuario usuario = (Usuario)  userDetails;

        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .toList();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("rol", roles)
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12))
                .signWith(getSignInKey())
                .compact();
    }

    // 2. Extraer username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 3. Validar token
    public boolean isTokenValid(String token, UserDetails userDetails) {

        // Si el token fue enviado a la lista negra por un Logout, muere aquí
        if (tokenBlacklistService.esTokenInvalido(token)){
            return false;
        }

        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // 4. Extraer cualquier claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())   // ← exige SecretKey
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    // ========== Privados ==========
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Instant getExpirationInstant(String token) {
        return extractExpiration(token).toInstant();
    }

    // ← CLAVE: SecretKey, NO Key
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public long getExpirationMillis(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }
}