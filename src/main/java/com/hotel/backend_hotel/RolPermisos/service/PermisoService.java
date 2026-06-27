package com.hotel.backend_hotel.RolPermisos.service;

import com.hotel.backend_hotel.RolPermisos.dto.PermisoRequest;
import com.hotel.backend_hotel.RolPermisos.dto.PermisoResponse;

import java.util.List;

public interface PermisoService {
    List<PermisoResponse> listarPermisos();
    PermisoResponse BuscarPorId(Long id);
    PermisoResponse CrearPermiso(PermisoRequest request);
    PermisoResponse ActualizarPermiso(Long id, PermisoRequest request);
    void eliminarPermiso(Long id);
}
