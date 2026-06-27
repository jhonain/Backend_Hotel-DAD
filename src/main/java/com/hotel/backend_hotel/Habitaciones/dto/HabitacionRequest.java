package com.hotel.backend_hotel.Habitaciones.dto;

import com.hotel.backend_hotel.Enums.EstadoHabitacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record HabitacionRequest(
        @NotBlank(message = "El número de habitación es obligatorio")
        String numero,

        @NotNull(message = "El piso es obligatorio")
        @Positive(message = "El piso debe ser mayor a 0")
        Integer piso,

        @NotBlank(message = "El tipo es obligatorio")
        String tipo,

        @NotNull(message = "La capacidad es obligatoria")
        @Positive(message = "La capacidad debe ser mayor a 0")
        Integer capacidad,

        @NotNull(message = "El precio es obligatorio")
        @PositiveOrZero(message = "El precio no puede ser negativo")
        Double precio,

        EstadoHabitacion estado, // Opcional, por defecto DISPONIBLE

        String descripcion,

        String imagenUrl // URL de Cloudinary
) {
}
