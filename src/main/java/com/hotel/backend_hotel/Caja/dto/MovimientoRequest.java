package com.hotel.backend_hotel.Caja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimientoRequest(
        @NotNull(message = "La caja es obligatoria")
        Long cajaId,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser positivo")
        Double monto,

        @NotBlank(message = "El concepto es obligatorio")
        String concepto
) {
}
