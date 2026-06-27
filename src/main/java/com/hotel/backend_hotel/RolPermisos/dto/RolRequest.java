package com.hotel.backend_hotel.RolPermisos.dto;

import jakarta.validation.constraints.NotBlank;

public record RolRequest(
        @NotBlank(message = "el nombre del rol es obligatorio")
        String nombre,

        String descripcion
) {
}
