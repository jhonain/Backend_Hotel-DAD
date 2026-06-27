package com.hotel.backend_hotel.Caja.dto;

import jakarta.validation.constraints.NotNull;

public record CajaRequest(
        @NotNull(message = "El monto inicial es obligatorio")
        Double montoInicial
) {
}
