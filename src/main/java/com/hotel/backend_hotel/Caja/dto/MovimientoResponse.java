package com.hotel.backend_hotel.Caja.dto;

import com.hotel.backend_hotel.Enums.TipoMovimiento;

public record MovimientoResponse(
        Long id,
        Long cajaId,
        Long reservaId,
        TipoMovimiento tipo,
        Double monto,
        String concepto,
        String fecha
) {
}
