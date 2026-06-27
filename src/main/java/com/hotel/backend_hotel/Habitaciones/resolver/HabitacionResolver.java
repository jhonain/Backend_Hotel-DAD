package com.hotel.backend_hotel.Habitaciones.resolver;

import com.hotel.backend_hotel.Enums.EstadoHabitacion;
import com.hotel.backend_hotel.Habitaciones.dto.HabitacionPage;
import com.hotel.backend_hotel.Habitaciones.dto.HabitacionRequest;
import com.hotel.backend_hotel.Habitaciones.dto.HabitacionResponse;
import com.hotel.backend_hotel.Habitaciones.service.HabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HabitacionResolver {

    private final HabitacionService habitacionService;

    @QueryMapping
    @PreAuthorize("hasAuthority('habitaciones:ver')")
    public List<HabitacionResponse> habitaciones() {
        return habitacionService.listarHabitaciones();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('habitaciones:ver')")
    public HabitacionResponse habitacion(@Argument Long id) {
        return habitacionService.buscarPorId(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('habitaciones:ver')")
    public HabitacionResponse habitacionPorNumero(@Argument String numero) {
        return habitacionService.buscarPorNumero(numero);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('habitaciones:ver')")
    public List<HabitacionResponse> habitacionesDisponibles() {
        return habitacionService.listarDisponibles();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('habitaciones:ver')")
    public List<HabitacionResponse> habitacionesPorTipo(@Argument String tipo) {
        return habitacionService.listarPorTipo(tipo);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('habitaciones:ver')")
    public List<HabitacionResponse> filtrarHabitaciones(
            @Argument EstadoHabitacion estado,
            @Argument String tipo,
            @Argument Integer piso,
            @Argument Integer capacidad,
            @Argument Double precioMin,
            @Argument Double precioMax
    ) {
        return habitacionService.filtrar(estado, tipo, piso, capacidad, precioMin, precioMax);
    }


    @QueryMapping
    @PreAuthorize("hasAuthority('habitaciones:ver')")
    public HabitacionPage habitacionesPaginadas(@Argument int page, @Argument int size) {
        return habitacionService.listarHabitacionPaginadas(page, size);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('habitaciones:crear')")
    public HabitacionResponse crearHabitacion(@Argument HabitacionRequest input) {
        return habitacionService.crearHabitacion(input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('habitaciones:editar')")
    public HabitacionResponse editarHabitacion(@Argument Long id, @Argument HabitacionRequest input) {
        return habitacionService.editarHabitacion(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('habitaciones:editar')")
    public HabitacionResponse cambiarEstadoHabitacion(@Argument Long id, @Argument EstadoHabitacion estado) {
        return habitacionService.cambiarEstado(id, estado);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('habitaciones:eliminar')")
    public Boolean eliminarHabitacion(@Argument Long id) {
        habitacionService.eliminarHabitacion(id);
        return true;
    }
}
