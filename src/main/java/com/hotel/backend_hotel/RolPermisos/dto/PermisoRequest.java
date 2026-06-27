package com.hotel.backend_hotel.RolPermisos.dto;

import jakarta.validation.constraints.NotBlank;

public record PermisoRequest (

        @NotBlank(message = "El código del permiso es obligatorio")
        String codigo,
        String descripcion,
        @NotBlank(message = "El módulo es obligatorio")
        String modulo

){
}
