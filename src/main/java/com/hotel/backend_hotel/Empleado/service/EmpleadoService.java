package com.hotel.backend_hotel.Empleado.service;

import com.hotel.backend_hotel.Empleado.dto.EmpleadoRequest;
import com.hotel.backend_hotel.Empleado.dto.EmpleadoResponse;
import com.hotel.backend_hotel.Enums.Cargo;
import com.hotel.backend_hotel.Enums.TurnoEmpleado;

import java.util.List;

public interface EmpleadoService {
    List<EmpleadoResponse> listarEmpleados();
    EmpleadoResponse buscarPorId(Long id);
    List<EmpleadoResponse> listarPorCargo(Cargo cargo);
    List<EmpleadoResponse> listarPorTurno(TurnoEmpleado turno);
    List<EmpleadoResponse> listarActivos();
    List<EmpleadoResponse> listarConPagoPendiente();
    EmpleadoResponse crearEmpleado(EmpleadoRequest request);
    EmpleadoResponse editarEmpleado(Long id, EmpleadoRequest request);
    void eliminarEmpleado(Long id);
    EmpleadoResponse cambiarEstado(Long id, Boolean activo);
    EmpleadoResponse asignarUsuario(Long empleadoId, Long usuarioId);
}
