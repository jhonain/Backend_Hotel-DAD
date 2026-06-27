package com.hotel.backend_hotel.Tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface TokenRevocadoRepository extends JpaRepository<TokenRevocado, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM TokenRevocado t WHERE t.expiracion < :ahora")
    int eliminarExpirados(Instant ahora);
}
