package com.hotel.backend_hotel.Huesped.dto;

public record HuespedResponse(
        Long id,
        String nombre,
        String apellido,
        String tipoDocumento,
        String numeroDocumento,
        String email,
        String telefono,
        String nacionalidad
) {
}
