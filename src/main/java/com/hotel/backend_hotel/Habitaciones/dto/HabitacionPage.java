package com.hotel.backend_hotel.Habitaciones.dto;


import java.util.List;

public record HabitacionPage(
        List<HabitacionResponse> items,
        long totalItems,
        int totalPages,
        int currentPage

) {
}