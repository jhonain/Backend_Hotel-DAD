package com.hotel.backend_hotel.Empleado.resolver;

import com.hotel.backend_hotel.Empleado.dto.EmpleadoRequest;
import com.hotel.backend_hotel.Empleado.dto.EmpleadoResponse;
import com.hotel.backend_hotel.Empleado.service.EmpleadoService;
import com.hotel.backend_hotel.Enums.Cargo;
import com.hotel.backend_hotel.Enums.TurnoEmpleado;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EmpleadoResolver {

    private final EmpleadoService empleadoService;

    // ========== QUERIES ==========

    @QueryMapping
    @PreAuthorize("hasAuthority('empleados:ver')")
    public List<EmpleadoResponse> empleados() {
        return empleadoService.listarEmpleados();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('empleados:ver')")
    public EmpleadoResponse empleado(@Argument Long id) {
        return empleadoService.buscarPorId(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('empleados:ver')")
    public List<EmpleadoResponse> empleadosPorCargo(@Argument Cargo cargo) {
        return empleadoService.listarPorCargo(cargo);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('empleados:ver')")
    public List<EmpleadoResponse> empleadosPorTurno(@Argument TurnoEmpleado turno) {
        return empleadoService.listarPorTurno(turno);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('empleados:ver')")
    public List<EmpleadoResponse> empleadosActivos() {
        return empleadoService.listarActivos();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('empleados:ver')")
    public List<EmpleadoResponse> empleadosConPagoPendiente() {
        return empleadoService.listarConPagoPendiente();
    }

    // ========== MUTATIONS ==========

    @MutationMapping
    @PreAuthorize("hasAuthority('empleados:crear')")
    public EmpleadoResponse crearEmpleado(@Argument EmpleadoRequest input) {
        return empleadoService.crearEmpleado(input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('empleados:editar')")
    public EmpleadoResponse editarEmpleado(@Argument Long id, @Argument EmpleadoRequest input) {
        return empleadoService.editarEmpleado(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('empleados:eliminar')")
    public Boolean eliminarEmpleado(@Argument Long id) {
        empleadoService.eliminarEmpleado(id);
        return true;
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('empleados:editar')")
    public EmpleadoResponse cambiarEstadoEmpleado(@Argument Long id, @Argument Boolean activo) {
        return empleadoService.cambiarEstado(id, activo);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('empleados:editar')")
    public EmpleadoResponse asignarUsuarioAEmpleado(@Argument Long empleadoId, @Argument Long usuarioId) {
        return empleadoService.asignarUsuario(empleadoId, usuarioId);
    }
}