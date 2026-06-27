package com.hotel.backend_hotel.Habitaciones.dto;

public record HabitacionResponse(
        Long id,
        String numero,
        Integer piso,
        String tipo,
        Integer capacidad,
        Double precio,
        String estado,
        String descripcion,
        String imagenUrl
) {
}
