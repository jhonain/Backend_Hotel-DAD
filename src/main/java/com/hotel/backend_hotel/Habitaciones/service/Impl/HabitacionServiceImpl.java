package com.hotel.backend_hotel.Habitaciones.service.Impl;

import com.hotel.backend_hotel.Enums.EstadoHabitacion;
import com.hotel.backend_hotel.Habitaciones.dto.HabitacionPage;
import com.hotel.backend_hotel.Habitaciones.dto.HabitacionRequest;
import com.hotel.backend_hotel.Habitaciones.dto.HabitacionResponse;
import com.hotel.backend_hotel.Habitaciones.entity.Habitacion;
import com.hotel.backend_hotel.Habitaciones.repository.HabitacionRepository;
import com.hotel.backend_hotel.Habitaciones.service.HabitacionService;
import com.hotel.backend_hotel.Huesped.dto.HuespedPage;
import com.hotel.backend_hotel.Huesped.dto.HuespedResponse;
import com.hotel.backend_hotel.Huesped.entity.Huesped;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionEmpresarial;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import com.hotel.backend_hotel.common.RealTime.NotificacionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final NotificacionResolver notificacionResolver;

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionResponse> listarHabitaciones() {
        return habitacionRepository.findAll().stream()
                .map(this::toHabitacionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionPage listarHabitacionPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("numero").ascending());
        Page<Habitacion> pageResult = habitacionRepository.findAll(pageable);

        List<HabitacionResponse> items = pageResult.getContent().stream()
                .map(this::toHabitacionResponse)
                .toList();

        return new HabitacionPage(items, pageResult.getTotalElements(),
                pageResult.getTotalPages(), pageResult.getNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionResponse buscarPorId(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Habitación no encontrada con id: " + id));
        return toHabitacionResponse(habitacion);
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionResponse buscarPorNumero(String numero) {
        Habitacion habitacion = habitacionRepository.findByNumero(numero)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Habitación no encontrada con número: " + numero));
        return toHabitacionResponse(habitacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionResponse> listarDisponibles() {
        return habitacionRepository.findByEstado(EstadoHabitacion.DISPONIBLE).stream()
                .map(this::toHabitacionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionResponse> listarPorTipo(String tipo) {
        return habitacionRepository.findByTipo(tipo).stream()
                .map(this::toHabitacionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionResponse> filtrar(EstadoHabitacion estado, String tipo, Integer piso,
                                            Integer capacidad, Double precioMin, Double precioMax) {
        return habitacionRepository.findAll().stream()
                .filter(h -> estado == null || h.getEstado() == estado)
                .filter(h -> tipo == null || h.getTipo().equalsIgnoreCase(tipo))
                .filter(h -> piso == null || h.getPiso().equals(piso))
                .filter(h -> capacidad == null || h.getCapacidad() >= capacidad)
                .filter(h -> precioMin == null || h.getPrecio() >= precioMin)
                .filter(h -> precioMax == null || h.getPrecio() <= precioMax)
                .map(this::toHabitacionResponse)
                .toList();
    }

    @Override
    @Transactional
    public HabitacionResponse crearHabitacion(HabitacionRequest request) {
        if (habitacionRepository.findByNumero(request.numero()).isPresent()) {
            throw new ExcepcionEmpresarial("Ya existe una habitación con el número: " + request.numero());
        }

        Habitacion habitacion = new Habitacion();
        habitacion.setNumero(request.numero());
        habitacion.setPiso(request.piso());
        habitacion.setTipo(request.tipo());
        habitacion.setCapacidad(request.capacidad());
        habitacion.setPrecio(request.precio());
        habitacion.setDescripcion(request.descripcion());
        habitacion.setImagenUrl(request.imagenUrl());
        habitacion.setEstado(request.estado() != null ? request.estado() : EstadoHabitacion.DISPONIBLE);

        habitacion = habitacionRepository.save(habitacion);
        notificacionResolver.emitiNotificacion("Habitación " + request.numero() + " creada", "HABITACIONES");
        return toHabitacionResponse(habitacion);
    }

    @Override
    @Transactional
    public HabitacionResponse editarHabitacion(Long id, HabitacionRequest request) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Habitación no encontrada con id: " + id));

        if (!habitacion.getNumero().equals(request.numero()) &&
                habitacionRepository.findByNumero(request.numero()).isPresent()) {
            throw new ExcepcionEmpresarial("Ya existe una habitación con el número: " + request.numero());
        }

        habitacion.setNumero(request.numero());
        habitacion.setPiso(request.piso());
        habitacion.setTipo(request.tipo());
        habitacion.setCapacidad(request.capacidad());
        habitacion.setPrecio(request.precio());
        habitacion.setDescripcion(request.descripcion());
        habitacion.setImagenUrl(request.imagenUrl());
        if (request.estado() != null) {
            habitacion.setEstado(request.estado());
        }

        habitacion = habitacionRepository.save(habitacion);
        notificacionResolver.emitiNotificacion("Habitación " + request.numero() + " actualizada", "HABITACIONES");
        return toHabitacionResponse(habitacion);
    }

    @Override
    @Transactional
    public HabitacionResponse cambiarEstado(Long id, EstadoHabitacion estado) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Habitación no encontrada con id: " + id));

        habitacion.setEstado(estado);

        if (estado == EstadoHabitacion.EN_LIMPIEZA) {
            habitacion.setLimpiezaInicio(LocalDateTime.now());
        } else if (estado == EstadoHabitacion.DISPONIBLE) {
            habitacion.setLimpiezaInicio(null);
        }

        habitacion = habitacionRepository.save(habitacion);
        notificacionResolver.emitiNotificacion("Habitación " + habitacion.getNumero() + " pasó a " + estado.name(), "HABITACIONES");
        return toHabitacionResponse(habitacion);
    }

    @Override
    @Transactional
    public void eliminarHabitacion(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Habitación no encontrada con id: " + id));
        notificacionResolver.emitiNotificacion("Habitación " + habitacion.getNumero() + " eliminada", "HABITACIONES");
        habitacionRepository.delete(habitacion);
    }

    private HabitacionResponse toHabitacionResponse(Habitacion habitacion) {
        return new HabitacionResponse(
                habitacion.getId(),
                habitacion.getNumero(),
                habitacion.getPiso(),
                habitacion.getTipo(),
                habitacion.getCapacidad(),
                habitacion.getPrecio(),
                habitacion.getEstado().name(),
                habitacion.getDescripcion(),
                habitacion.getImagenUrl()
        );
    }
}
