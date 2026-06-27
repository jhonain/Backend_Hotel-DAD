package com.hotel.backend_hotel.Reserva.dto;

import java.util.List;

public record ReservaPage(
        List<ReservaResponse> items,
        long totalItems,
        int totalPages,
        int currentPage
) {
}
