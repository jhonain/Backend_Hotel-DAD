package com.hotel.backend_hotel.Reserva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservaRequest(
        @NotNull(message = "El huésped es obligatorio")
        Long huespedId,

        @NotNull(message = "La habitación es obligatoria")
        Long habitacionId,

        @NotNull(message = "El empleado es obligatorio")
        Long empleadoId,

        @NotBlank(message = "El check-in es obligatorio")
        String checkIn,

        @NotBlank(message = "El check-out es obligatorio")
        String checkOut,

        @NotNull(message = "El número de personas es obligatorio")
        @Positive(message = "Debe haber al menos 1 persona")
        Integer numeroPersonas
) {
}
