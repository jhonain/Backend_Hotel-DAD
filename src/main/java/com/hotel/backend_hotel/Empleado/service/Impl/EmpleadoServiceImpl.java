package com.hotel.backend_hotel.Empleado.service.Impl;

import com.hotel.backend_hotel.Empleado.dto.EmpleadoRequest;
import com.hotel.backend_hotel.Empleado.dto.EmpleadoResponse;
import com.hotel.backend_hotel.Empleado.entity.Empleado;
import com.hotel.backend_hotel.Empleado.repository.EmpleadoRepository;
import com.hotel.backend_hotel.Empleado.service.EmpleadoService;
import com.hotel.backend_hotel.Enums.Cargo;
import com.hotel.backend_hotel.Enums.TurnoEmpleado;
import com.hotel.backend_hotel.Auth.entity.Usuario;
import com.hotel.backend_hotel.Auth.repository.UserRepository;
import com.hotel.backend_hotel.RolPermisos.entity.Rol;
import com.hotel.backend_hotel.RolPermisos.repository.RolRepository;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listarEmpleados() {
        return empleadoRepository.findAll().stream()
                .map(this::toEmpleadoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponse buscarPorId(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Empleado no encontrado con id: " + id));
        return toEmpleadoResponse(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listarPorCargo(Cargo cargo) {
        return empleadoRepository.findByCargo(cargo).stream()
                .map(this::toEmpleadoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listarPorTurno(TurnoEmpleado turno) {
        return empleadoRepository.findByTurno(turno).stream()
                .map(this::toEmpleadoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listarActivos() {
        return empleadoRepository.findByActivo(true).stream()
                .map(this::toEmpleadoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listarConPagoPendiente() {
        return empleadoRepository.findByActivo(true).stream()
                .filter(e -> e.getPagoPendiente() || e.getDiasParaPago() <= 3)
                .map(this::toEmpleadoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmpleadoResponse crearEmpleado(EmpleadoRequest request) {
        // 1. Generar username único: nombre.apellido [+ sufijo si existe]
        String baseUsername = request.nombre().toLowerCase() + "." + request.apellido().toLowerCase();
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix;
            suffix++;
        }

        // 2. Buscar o crear rol según el cargo
        String nombreRol = request.cargo().name();
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseGet(() -> rolRepository.save(
                        Rol.builder()
                                .nombre(nombreRol)
                                .descripcion("Rol auto-creado para cargo " + nombreRol)
                                .permisos(new HashSet<>())
                                .build()
                ));

        // 3. Crear Usuario (email null)
        Usuario usuario = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(username))
                .email(null)
                .enabled(true)
                .roles(new HashSet<>())
                .build();
        usuario.getRoles().add(rol);
        usuario = userRepository.save(usuario);

        // 4. Crear Empleado vinculado al usuario
        Empleado empleado = new Empleado();
        empleado.setNombre(request.nombre());
        empleado.setApellido(request.apellido());
        empleado.setCargo(request.cargo());
        empleado.setTelefono(request.telefono());
        empleado.setTurno(request.turno());
        empleado.setSalario(request.salario());
        empleado.setActivo(true);
        empleado.setUsuario(usuario);

        if (request.fechaContratacion() != null) {
            empleado.setFechaContratacion(LocalDate.parse(request.fechaContratacion()));
        } else {
            empleado.setFechaContratacion(LocalDate.now());
        }

        empleado = empleadoRepository.save(empleado);
        return toEmpleadoResponse(empleado);
    }

    @Override
    @Transactional
    public EmpleadoResponse editarEmpleado(Long id, EmpleadoRequest request) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Empleado no encontrado con id: " + id));

        empleado.setNombre(request.nombre());
        empleado.setApellido(request.apellido());
        empleado.setCargo(request.cargo());
        empleado.setTelefono(request.telefono());
        empleado.setTurno(request.turno());
        empleado.setSalario(request.salario());

        if (request.fechaContratacion() != null) {
            empleado.setFechaContratacion(LocalDate.parse(request.fechaContratacion()));
        }

        empleado = empleadoRepository.save(empleado);
        return toEmpleadoResponse(empleado);
    }

    @Override
    @Transactional
    public void eliminarEmpleado(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Empleado no encontrado con id: " + id));

        empleado.setUsuario(null);
        empleadoRepository.save(empleado);
        empleadoRepository.delete(empleado);
    }

    @Override
    @Transactional
    public EmpleadoResponse cambiarEstado(Long id, Boolean activo) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Empleado no encontrado con id: " + id));
        empleado.setActivo(activo);
        empleado = empleadoRepository.save(empleado);
        return toEmpleadoResponse(empleado);
    }

    @Override
    @Transactional
    public EmpleadoResponse asignarUsuario(Long empleadoId, Long usuarioId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Empleado no encontrado"));

        if (empleadoRepository.existsByUsuarioId(usuarioId)) {
            throw new RuntimeException("El usuario ya está asignado a otro empleado");
        }

        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Usuario no encontrado"));

        empleado.setUsuario(usuario);
        empleado = empleadoRepository.save(empleado);
        return toEmpleadoResponse(empleado);
    }

    // ========== MAPPER ==========

    private EmpleadoResponse toEmpleadoResponse(Empleado empleado) {
        EmpleadoResponse.UsuarioInfo usuarioInfo = null;
        if (empleado.getUsuario() != null) {
            usuarioInfo = new EmpleadoResponse.UsuarioInfo(
                    empleado.getUsuario().getId(),
                    empleado.getUsuario().getUsername(),
                    empleado.getUsuario().getEmail()
            );
        }

        return new EmpleadoResponse(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getCargo().name(),
                empleado.getTelefono(),
                empleado.getTurno().name(),
                empleado.getActivo(),
                empleado.getFechaContratacion() != null ? empleado.getFechaContratacion().toString() : null,
                empleado.getSalario(),
                empleado.getProximoPago() != null ? empleado.getProximoPago().toString() : null,
                empleado.getDiasParaPago(),
                empleado.getEstadoPago(),
                empleado.getPagoPendiente(),
                empleado.getSalarioPendiente(),
                usuarioInfo
        );
    }
}