package com.hotel.backend_hotel.ServiciosAdicionales.service.Impl;

import com.hotel.backend_hotel.Enums.CategoriaServicio;
import com.hotel.backend_hotel.ServiciosAdicionales.dto.ServicioAdicionalRequest;
import com.hotel.backend_hotel.ServiciosAdicionales.dto.ServicioAdicionalResponse;
import com.hotel.backend_hotel.ServiciosAdicionales.entity.ServicioAdicional;
import com.hotel.backend_hotel.ServiciosAdicionales.repository.ServicioAdicionalRepository;
import com.hotel.backend_hotel.ServiciosAdicionales.service.ServicioAdicionalService;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionEmpresarial;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioAdicionalServiceImpl implements ServicioAdicionalService {

    private final ServicioAdicionalRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ServicioAdicionalResponse> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioAdicionalResponse buscarPorId(Long id) {
        ServicioAdicional s = repository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Servicio no encontrado con id: " + id));
        return toResponse(s);
    }

    @Override
    @Transactional
    public ServicioAdicionalResponse crear(ServicioAdicionalRequest request) {
        if (repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ExcepcionEmpresarial("Ya existe un servicio con el nombre: " + request.nombre());
        }
        ServicioAdicional s = new ServicioAdicional();
        s.setNombre(request.nombre());
        s.setDescripcion(request.descripcion());
        s.setPrecio(request.precio());
        s.setCategoria(request.categoria());
        s.setDisponible(request.disponible() != null ? request.disponible() : true);
        s = repository.save(s);
        return toResponse(s);
    }

    @Override
    @Transactional
    public ServicioAdicionalResponse editar(Long id, ServicioAdicionalRequest request) {
        ServicioAdicional s = repository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Servicio no encontrado con id: " + id));
        if (!s.getNombre().equalsIgnoreCase(request.nombre()) &&
                repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ExcepcionEmpresarial("Ya existe otro servicio con el nombre: " + request.nombre());
        }
        s.setNombre(request.nombre());
        s.setDescripcion(request.descripcion());
        s.setPrecio(request.precio());
        s.setCategoria(request.categoria());
        s.setDisponible(request.disponible() != null ? request.disponible() : true);
        s = repository.save(s);
        return toResponse(s);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ServicioAdicional s = repository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Servicio no encontrado con id: " + id));
        repository.delete(s);
    }

    private ServicioAdicionalResponse toResponse(ServicioAdicional s) {
        return new ServicioAdicionalResponse(
                s.getId(),
                s.getNombre(),
                s.getDescripcion(),
                s.getPrecio(),
                s.getCategoria().name(),
                s.getDisponible()
        );
    }
}
