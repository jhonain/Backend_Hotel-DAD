package com.hotel.backend_hotel.Caja.dto;

import com.hotel.backend_hotel.Enums.EstadoCaja;
import com.hotel.backend_hotel.Enums.TurnoEmpleado;

public record CajaResponse(
        Long id,
        String codigo,
        Long empleadoId,
        String empleadoNombre,
        String fechaApertura,
        String fechaCierre,
        Double montoInicial,
        Double montoFinal,
        Double totalIngresos,
        Double totalEgresos,
        EstadoCaja estado,
        TurnoEmpleado turno
) {
}
