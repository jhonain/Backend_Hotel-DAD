package com.hotel.backend_hotel.Caja.dto;

import java.util.List;

public record CajaPage(
        List<CajaResponse> items,
        long totalItems,
        int totalPages,
        int currentPage
) {}
