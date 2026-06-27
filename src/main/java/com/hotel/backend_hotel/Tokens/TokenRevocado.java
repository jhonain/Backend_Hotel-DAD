package com.hotel.backend_hotel.Tokens;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "tokens_revocados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRevocado {

    @Id
    private String tokenHash;

    private Instant fechaRevocacion;

    private Instant expiracion;
}