package com.hotel.backend_hotel.Huesped.resolver;

import com.hotel.backend_hotel.Habitaciones.dto.HabitacionPage;
import com.hotel.backend_hotel.Huesped.dto.HuespedPage;
import com.hotel.backend_hotel.Huesped.dto.HuespedRequest;
import com.hotel.backend_hotel.Huesped.dto.HuespedResponse;
import com.hotel.backend_hotel.Huesped.service.HuespedService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HuespedResolver {

    private final HuespedService huespedService;

    @QueryMapping
    @PreAuthorize("hasAuthority('huespedes:ver')")
    public List<HuespedResponse> huespedes() {
        return huespedService.listarHuespedes();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('huespedes:ver')")
    public HuespedPage huespedesPaginados(@Argument int page, @Argument int size) {
        return huespedService.listarHuespedesPaginados(page, size);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('huespedes:ver')")
    public HuespedResponse huesped(@Argument Long id) {
        return huespedService.buscarPorId(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('huespedes:ver')")
    public HuespedResponse huespedPorDocumento(@Argument String numeroDocumento) {
        return huespedService.buscarPorDocumento(numeroDocumento);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('huespedes:crear')")
    public HuespedResponse crearHuesped(@Argument HuespedRequest input) {

        return huespedService.crearHuesped(input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('huespedes:editar')")
    public HuespedResponse editarHuesped(@Argument Long id, @Argument HuespedRequest input) {
        return huespedService.editarHuesped(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('huespedes:eliminar')")
    public Boolean eliminarHuesped(@Argument Long id) {
        huespedService.eliminarHuesped(id);
        return true;
    }
}
