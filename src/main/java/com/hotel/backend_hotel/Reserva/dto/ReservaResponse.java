package com.hotel.backend_hotel.Reserva.dto;

import com.hotel.backend_hotel.Enums.EstadoReserva;
import com.hotel.backend_hotel.Enums.MetodoPago;

public record ReservaResponse(
        Long id,
        Long huespedId,
        String huespedNombre,
        Long habitacionId,
        String habitacionNumero,
        Long empleadoId,
        String empleadoNombre,
        String checkIn,
        String checkOut,
        Integer numeroPersonas,
        String fechaCheckIn,
        Double totalPagar,
        MetodoPago metodoPago,
        EstadoReserva estado
) {
}
