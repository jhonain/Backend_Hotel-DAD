package com.hotel.backend_hotel.Caja.dto;

import com.hotel.backend_hotel.Enums.MetodoPago;

public record PagoResponse(
        Long id,
        Long reservaId,
        Double monto,
        MetodoPago metodoPago,
        String fechaPago
) {
}
