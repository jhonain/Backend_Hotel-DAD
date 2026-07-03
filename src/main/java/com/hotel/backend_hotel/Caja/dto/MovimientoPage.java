package com.hotel.backend_hotel.Caja.dto;

import java.util.List;

public record MovimientoPage(
        List<MovimientoResponse> items,
        long totalItems,
        int totalPages,
        int currentPage
) {}
