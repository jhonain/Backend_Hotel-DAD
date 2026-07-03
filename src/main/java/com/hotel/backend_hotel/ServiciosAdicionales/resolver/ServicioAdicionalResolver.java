package com.hotel.backend_hotel.ServiciosAdicionales.resolver;

import com.hotel.backend_hotel.ServiciosAdicionales.dto.ServicioAdicionalRequest;
import com.hotel.backend_hotel.ServiciosAdicionales.dto.ServicioAdicionalResponse;
import com.hotel.backend_hotel.ServiciosAdicionales.service.ServicioAdicionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ServicioAdicionalResolver {

    private final ServicioAdicionalService service;

    @QueryMapping
    @PreAuthorize("hasAuthority('servicios_adicionales:ver')")
    public List<ServicioAdicionalResponse> serviciosAdicionales() {
        return service.listarTodos();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('servicios_adicionales:ver')")
    public ServicioAdicionalResponse servicioAdicional(@Argument Long id) {
        return service.buscarPorId(id);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('servicios_adicionales:crear')")
    public ServicioAdicionalResponse crearServicioAdicional(@Argument ServicioAdicionalRequest input) {
        return service.crear(input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('servicios_adicionales:editar')")
    public ServicioAdicionalResponse editarServicioAdicional(@Argument Long id, @Argument ServicioAdicionalRequest input) {
        return service.editar(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('servicios_adicionales:eliminar')")
    public Boolean eliminarServicioAdicional(@Argument Long id) {
        service.eliminar(id);
        return true;
    }
}
