package com.hotel.backend_hotel.RolPermisos.service;

import com.hotel.backend_hotel.RolPermisos.dto.PermisoResponse;
import com.hotel.backend_hotel.RolPermisos.dto.RolRequest;
import com.hotel.backend_hotel.RolPermisos.dto.RolResponse;

import java.util.List;

public interface RolService {

    List<RolResponse> ListarRoles();
    RolResponse BuscarPorID(Long id);
    RolResponse CrearRol(RolRequest rolRequest);
    RolResponse ActualizarRol(Long id, String nombre, String descripcion);
    RolResponse asignarPermisos(Long rolId, List<Long> permisosIds);
    void EliminarRol(Long Id);
    RolResponse RevocarPermiso(Long rolId, Long permisoId);
}
