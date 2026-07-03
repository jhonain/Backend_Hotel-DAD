package com.hotel.backend_hotel.Facturacion.dto;

import java.util.List;

public record FacturaPage(
        List<FacturaResponse> items,
        long totalItems,
        int totalPages,
        int currentPage
) {}
