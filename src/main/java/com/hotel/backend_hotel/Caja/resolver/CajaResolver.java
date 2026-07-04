package com.hotel.backend_hotel.Caja.resolver;

import com.hotel.backend_hotel.Caja.dto.*;
import com.hotel.backend_hotel.Caja.service.CajaService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CajaResolver {

    private final CajaService cajaService;

    @QueryMapping
    @PreAuthorize("hasAuthority('caja:ver')")
    public List<CajaResponse> cajas() {
        return cajaService.listarTodas();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('caja:ver')")
    public CajaResponse caja(@Argument Long id) {
        return cajaService.buscarPorId(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('caja:ver')")
    public CajaResponse cajaAbierta() {
        return cajaService.buscarCajaAbierta();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('caja:ver')")
    public List<CajaResponse> cajasPorFecha(@Argument String inicio, @Argument String fin) {
        return cajaService.listarPorFecha(inicio, fin);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('caja:ver')")
    public List<MovimientoResponse> movimientosDeCaja(@Argument Long cajaId) {
        return cajaService.movimientosDeCaja(cajaId);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('caja:ver')")
    public MovimientoPage movimientosDeCajaPaginados(@Argument Long cajaId, @Argument int page, @Argument int size) {
        return cajaService.movimientosDeCajaPaginados(cajaId, page, size);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('caja:ver')")
    public ResumenCaja resumenCaja(@Argument String inicio, @Argument String fin) {
        return cajaService.resumenCaja(inicio, fin);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('caja:ver')")
    public CajaPage cajasPorFechaPaginado(@Argument String inicio, @Argument String fin, @Argument int page, @Argument int size) {
        return cajaService.listarPorFechaPaginado(inicio, fin, page, size);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('caja:crear')")
    public CajaResponse abrirCaja(@Argument Double montoInicial) {
        return cajaService.abrirCaja(montoInicial);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('caja:editar')")
    public CajaResponse cerrarCaja(@Argument Long id, @Argument Double montoFinal) {
        return cajaService.cerrarCaja(id, montoFinal);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('caja:crear')")
    public MovimientoResponse registrarEgreso(@Argument MovimientoRequest input) {
        return cajaService.registrarEgreso(input);
    }
}
