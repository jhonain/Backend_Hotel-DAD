package com.hotel.backend_hotel.ServiciosAdicionales.dto;

import com.hotel.backend_hotel.Enums.CategoriaServicio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ServicioAdicionalRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        String descripcion,
        @NotNull(message = "El precio es obligatorio") @Positive(message = "El precio debe ser positivo") Double precio,
        @NotNull(message = "La categoría es obligatoria") CategoriaServicio categoria,
        Boolean disponible
) {}
