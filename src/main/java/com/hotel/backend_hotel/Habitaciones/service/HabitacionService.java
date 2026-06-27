package com.hotel.backend_hotel.Habitaciones.service;

import com.hotel.backend_hotel.Enums.EstadoHabitacion;
import com.hotel.backend_hotel.Habitaciones.dto.HabitacionPage;
import com.hotel.backend_hotel.Habitaciones.dto.HabitacionRequest;
import com.hotel.backend_hotel.Habitaciones.dto.HabitacionResponse;
import com.hotel.backend_hotel.Huesped.dto.HuespedPage;

import java.util.List;

public interface HabitacionService {
    List<HabitacionResponse> listarHabitaciones();
    HabitacionPage listarHabitacionPaginadas(int page, int size);
    HabitacionResponse buscarPorId(Long id);
    HabitacionResponse buscarPorNumero(String numero);
    List<HabitacionResponse> listarDisponibles();
    List<HabitacionResponse> listarPorTipo(String tipo);
    List<HabitacionResponse> filtrar(EstadoHabitacion estado, String tipo, Integer piso, Integer capacidad, Double precioMin, Double precioMax);
    HabitacionResponse crearHabitacion(HabitacionRequest request);
    HabitacionResponse editarHabitacion(Long id, HabitacionRequest request);
    HabitacionResponse cambiarEstado(Long id, EstadoHabitacion estado);
    void eliminarHabitacion(Long id);
}
