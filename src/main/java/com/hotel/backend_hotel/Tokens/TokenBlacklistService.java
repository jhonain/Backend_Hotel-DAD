package com.hotel.backend_hotel.Tokens;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hotel.backend_hotel.Security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenBlacklistService {

    private final TokenRevocadoRepository tokenRevocadoRepository;
    private final JwtUtils jwtUtils;

    // Constructor manual con @Lazy para romper el círculo: JwtUtils ↔ TokenBlacklistService
    public TokenBlacklistService(TokenRevocadoRepository tokenRevocadoRepository,
                                 @Lazy JwtUtils jwtUtils) {
        this.tokenRevocadoRepository = tokenRevocadoRepository;
        this.jwtUtils = jwtUtils;
    }

    private final Cache<String, Boolean> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    public void revocarToken(String token) {
        String hash = DigestUtils.sha256Hex(token);
        Instant expiracion = jwtUtils.getExpirationInstant(token);  // ← ahora sí compila

        TokenRevocado revocado = TokenRevocado.builder()
                .tokenHash(hash)
                .fechaRevocacion(Instant.now())
                .expiracion(expiracion)
                .build();

        tokenRevocadoRepository.save(revocado);
        log.info("🔒 Token revocado y persistido en PostgreSQL");

        cache.put(hash, true);
    }

    public boolean esTokenInvalido(String token) {
        String hash = DigestUtils.sha256Hex(token);

        Boolean enCache = cache.getIfPresent(hash);
        if (enCache != null) {
            return true;
        }

        boolean enBD = tokenRevocadoRepository.existsById(hash);
        if (enBD) {
            cache.put(hash, true);
        }
        return enBD;
    }

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    public void limpiarTokensExpirados() {
        int eliminados = tokenRevocadoRepository.eliminarExpirados(Instant.now());
        log.info("🧹 Limpieza de tokens revocados expirados: {} eliminados", eliminados);
    }
}