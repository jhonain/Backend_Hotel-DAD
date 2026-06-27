package com.hotel.backend_hotel.RolPermisos.dto;

import java.util.List;

public record RolResponse(
        Long id,
        String nombre,
        String descripcion,
        List<PermisoResponse> permisos

) {
}
