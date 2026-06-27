package com.hotel.backend_hotel.Huesped.dto;

import java.util.List;

public record HuespedPage(
        List<HuespedResponse> items,
        long totalItems,
        int totalPages,
        int currentPage
) {
}
