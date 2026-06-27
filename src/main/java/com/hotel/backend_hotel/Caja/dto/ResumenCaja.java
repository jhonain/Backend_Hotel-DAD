package com.hotel.backend_hotel.Caja.dto;

import java.util.List;

public record ResumenCaja(
        long totalCajas,
        long cajasAbiertas,
        double totalIngresos,
        double totalEgresos,
        double saldoTotal,
        List<CajaResponse> cajas
) {
}
