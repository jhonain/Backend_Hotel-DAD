package com.hotel.backend_hotel.RolPermisos.service.Impl;

import com.hotel.backend_hotel.RolPermisos.dto.PermisoResponse;
import com.hotel.backend_hotel.RolPermisos.dto.RolRequest;
import com.hotel.backend_hotel.RolPermisos.dto.RolResponse;
import com.hotel.backend_hotel.RolPermisos.entity.Permiso;
import com.hotel.backend_hotel.RolPermisos.entity.Rol;
import com.hotel.backend_hotel.RolPermisos.repository.PermisoRepository;
import com.hotel.backend_hotel.RolPermisos.repository.RolRepository;
import com.hotel.backend_hotel.RolPermisos.service.RolService;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    @Override
    @Transactional
    public List<RolResponse> ListarRoles() {
        return rolRepository.findAll().stream()
                .map(this::toRolResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RolResponse BuscarPorID(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Rol no encontrado con id: " + id));
        return toRolResponse(rol);
    }

    @Override
    public RolResponse CrearRol(RolRequest rolRequest) {
        Rol rol = new Rol();
        rol.setNombre(rolRequest.nombre());
        rol.setDescripcion(rolRequest.descripcion());
        rol =  rolRepository.save(rol);
        return new RolResponse(rol.getId(), rol.getNombre(), rol.getDescripcion(), List.of());
    }

    @Override
    @Transactional
    public RolResponse ActualizarRol(Long id, String nombre, String descripcion) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Rol no encontrado con id: " + id));

        if (nombre != null) rol.setNombre(nombre);
        if (descripcion != null) rol.setDescripcion(descripcion);

        rol = rolRepository.save(rol);
        return toRolResponse(rol);
    }

    @Override
    public RolResponse asignarPermisos(Long rolId, List<Long> permisosIds) {

        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(()-> new ExcepcionNoEncontrada("Rol no encontrado"));

        List<Permiso> permisos = permisoRepository.findAllById(permisosIds);
        rol.setPermisos(new java.util.HashSet<>(permisos));
        rol = rolRepository.save(rol);
        List<PermisoResponse> permisosRes = rol.getPermisos().stream()
                .map(p -> new PermisoResponse(p.getId(), p.getCodigo(), p.getDescripcion(), p.getModulo()))
                .toList();

        return new RolResponse(rol.getId(), rol.getNombre(), rol.getDescripcion(), permisosRes);
    }

    @Override
    public void EliminarRol(Long Id) {
        Rol rol = rolRepository.findById(Id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Rol no encontrado con id: " + Id));

        // Opcional: verificar que no tenga usuarios asignados
        if (!rol.getUsuarios().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el rol porque tiene usuarios asignados");
        }

        rolRepository.delete(rol);
    }

    @Override
    @Transactional
    public RolResponse RevocarPermiso(Long rolId, Long permisoId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Rol no encontrado con id: " + rolId));

        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Permiso no encontrado con id: " + permisoId));

        rol.getPermisos().remove(permiso);
        rol = rolRepository.save(rol);
        return toRolResponse(rol);
    }


    // ========== MAPPERS (privados) ==========

    private RolResponse toRolResponse(Rol rol) {
        List<PermisoResponse> permisos = rol.getPermisos().stream()
                .map(this::toPermisoResponse)
                .collect(toList());

        return new RolResponse(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion(),
                permisos
        );
    }

    private PermisoResponse toPermisoResponse(Permiso permiso) {
        return new PermisoResponse(
                permiso.getId(),
                permiso.getCodigo(),
                permiso.getDescripcion(),
                permiso.getModulo()
        );
    }
}
