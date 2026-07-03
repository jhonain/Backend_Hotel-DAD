package com.hotel.backend_hotel.ServiciosAdicionales.dto;

public record ServicioAdicionalResponse(
        Long id,
        String nombre,
        String descripcion,
        Double precio,
        String categoria,
        Boolean disponible
) {}
