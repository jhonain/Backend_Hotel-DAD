package com.hotel.backend_hotel.Reserva.service;

import com.hotel.backend_hotel.Enums.MetodoPago;
import com.hotel.backend_hotel.Reserva.dto.ReservaPage;
import com.hotel.backend_hotel.Reserva.dto.ReservaRequest;
import com.hotel.backend_hotel.Reserva.dto.ReservaResponse;

import java.util.List;

public interface ReservaService {
    List<ReservaResponse> listarReservas();
    ReservaPage listarReservasPaginadas(int page, int size);
    ReservaResponse buscarPorId(Long id);
    List<ReservaResponse> buscarPorHuespedId(Long huespedId);
    List<ReservaResponse> buscarPorHabitacionId(Long habitacionId);
    List<ReservaResponse> buscarPorEstado(String estado);
    List<ReservaResponse> buscarPorEmpleadoId(Long empleadoId);
    List<ReservaResponse> buscarPorEmpleadoYFecha(Long empleadoId, String inicio, String fin);
    long contarPorEmpleadoYFecha(Long empleadoId, String inicio, String fin);
    ReservaResponse crearReserva(ReservaRequest request);
    ReservaResponse editarReserva(Long id, ReservaRequest request);
    ReservaResponse checkinReserva(Long id, MetodoPago metodoPago);
    ReservaResponse checkoutReserva(Long id);
    ReservaResponse cancelarReserva(Long id);
    void eliminarReserva(Long id);
}
