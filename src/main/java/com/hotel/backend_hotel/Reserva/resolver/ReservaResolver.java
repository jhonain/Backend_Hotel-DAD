package com.hotel.backend_hotel.Reserva.resolver;

import com.hotel.backend_hotel.Enums.MetodoPago;
import com.hotel.backend_hotel.Reserva.dto.ReservaPage;
import com.hotel.backend_hotel.Reserva.dto.ReservaRequest;
import com.hotel.backend_hotel.Reserva.dto.ReservaResponse;
import com.hotel.backend_hotel.Reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReservaResolver {

    private final ReservaService reservaService;

    @QueryMapping
    @PreAuthorize("hasAuthority('reservas:ver')")
    public List<ReservaResponse> reservas() {
        return reservaService.listarReservas();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('reservas:ver')")
    public ReservaPage reservasPaginadas(@Argument int page, @Argument int size) {
        return reservaService.listarReservasPaginadas(page, size);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('reservas:ver')")
    public ReservaResponse reserva(@Argument Long id) {
        return reservaService.buscarPorId(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('reservas:ver')")
    public List<ReservaResponse> reservasPorHuesped(@Argument Long huespedId) {
        return reservaService.buscarPorHuespedId(huespedId);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('reservas:ver')")
    public List<ReservaResponse> reservasPorHabitacion(@Argument Long habitacionId) {
        return reservaService.buscarPorHabitacionId(habitacionId);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('reservas:ver')")
    public List<ReservaResponse> reservasPorEstado(@Argument String estado) {
        return reservaService.buscarPorEstado(estado);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('reservas:ver')")
    public List<ReservaResponse> reservasPorEmpleado(@Argument Long empleadoId) {
        return reservaService.buscarPorEmpleadoId(empleadoId);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('reservas:ver')")
    public List<ReservaResponse> reservasPorEmpleadoYFecha(
            @Argument Long empleadoId,
            @Argument String inicio,
            @Argument String fin) {
        return reservaService.buscarPorEmpleadoYFecha(empleadoId, inicio, fin);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('reservas:ver')")
    public long conteoReservasPorEmpleadoYFecha(
            @Argument Long empleadoId,
            @Argument String inicio,
            @Argument String fin) {
        return reservaService.contarPorEmpleadoYFecha(empleadoId, inicio, fin);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('reservas:crear')")
    public ReservaResponse crearReserva(@Argument ReservaRequest input) {
        return reservaService.crearReserva(input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('reservas:editar')")
    public ReservaResponse editarReserva(@Argument Long id, @Argument ReservaRequest input) {
        return reservaService.editarReserva(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('reservas:editar')")
    public ReservaResponse checkinReserva(@Argument Long id, @Argument MetodoPago metodoPago) {
        return reservaService.checkinReserva(id, metodoPago);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('reservas:editar')")
    public ReservaResponse checkoutReserva(@Argument Long id) {
        return reservaService.checkoutReserva(id);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('reservas:editar')")
    public ReservaResponse cancelarReserva(@Argument Long id) {
        return reservaService.cancelarReserva(id);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('reservas:eliminar')")
    public Boolean eliminarReserva(@Argument Long id) {
        reservaService.eliminarReserva(id);
        return true;
    }
}
