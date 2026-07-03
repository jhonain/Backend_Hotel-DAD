package com.hotel.backend_hotel.ServiciosAdicionales.service;

import com.hotel.backend_hotel.ServiciosAdicionales.dto.ServicioAdicionalRequest;
import com.hotel.backend_hotel.ServiciosAdicionales.dto.ServicioAdicionalResponse;

import java.util.List;

public interface ServicioAdicionalService {
    List<ServicioAdicionalResponse> listarTodos();
    ServicioAdicionalResponse buscarPorId(Long id);
    ServicioAdicionalResponse crear(ServicioAdicionalRequest request);
    ServicioAdicionalResponse editar(Long id, ServicioAdicionalRequest request);
    void eliminar(Long id);
}
