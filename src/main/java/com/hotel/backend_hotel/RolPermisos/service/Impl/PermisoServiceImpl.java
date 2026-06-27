package com.hotel.backend_hotel.RolPermisos.service.Impl;

import com.hotel.backend_hotel.RolPermisos.dto.PermisoRequest;
import com.hotel.backend_hotel.RolPermisos.dto.PermisoResponse;
import com.hotel.backend_hotel.RolPermisos.entity.Permiso;
import com.hotel.backend_hotel.RolPermisos.repository.PermisoRepository;
import com.hotel.backend_hotel.RolPermisos.service.PermisoService;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermisoServiceImpl implements PermisoService {

    private final PermisoRepository permisoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponse> listarPermisos() {
        return permisoRepository.findAll().stream()
                .map(this::toPermisoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PermisoResponse BuscarPorId(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Permiso no encontrado con id: " + id));
        return toPermisoResponse(permiso);
    }

    @Override
    @Transactional
    public PermisoResponse CrearPermiso(PermisoRequest request) {
        // Validar que no exista el código
        if (permisoRepository.existsByCodigo(request.codigo())) {
            throw new RuntimeException("Ya existe un permiso con el código: " + request.codigo());
        }

        Permiso permiso = new Permiso();
        permiso.setCodigo(request.codigo());
        permiso.setDescripcion(request.descripcion());
        permiso.setModulo(request.modulo());

        permiso = permisoRepository.save(permiso);
        return toPermisoResponse(permiso);
    }

    @Override
    @Transactional
    public PermisoResponse ActualizarPermiso(Long id, PermisoRequest request) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Permiso no encontrado con id: " + id));

        // Si cambia el código, validar que no exista
        if (!permiso.getCodigo().equals(request.codigo()) &&
                permisoRepository.existsByCodigo(request.codigo())) {
            throw new RuntimeException("Ya existe un permiso con el código: " + request.codigo());
        }

        permiso.setCodigo(request.codigo());
        permiso.setDescripcion(request.descripcion());
        permiso.setModulo(request.modulo());

        permiso = permisoRepository.save(permiso);
        return toPermisoResponse(permiso);
    }

    @Override
    @Transactional
    public void eliminarPermiso(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Permiso no encontrado con id: " + id));

        // Validar que no esté asignado a ningún rol
        if (!permiso.getRoles().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el permiso porque está asignado a roles");
        }

        permisoRepository.delete(permiso);
    }

    // ========== MAPPER ==========

    private PermisoResponse toPermisoResponse(Permiso permiso) {
        return new PermisoResponse(
                permiso.getId(),
                permiso.getCodigo(),
                permiso.getDescripcion(),
                permiso.getModulo()
        );
    }
}