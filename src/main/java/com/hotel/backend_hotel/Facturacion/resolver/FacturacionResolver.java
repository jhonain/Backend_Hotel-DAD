package com.hotel.backend_hotel.Facturacion.resolver;

import com.hotel.backend_hotel.Enums.EstadoFactura;
import com.hotel.backend_hotel.Enums.TipoComprobante;
import com.hotel.backend_hotel.Facturacion.dto.*;
import com.hotel.backend_hotel.Facturacion.service.FacturacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FacturacionResolver {

    private final FacturacionService facturacionService;

    @QueryMapping
    @PreAuthorize("hasAuthority('facturacion:ver')")
    public EmisorResponse emisorActivo() {
        return facturacionService.buscarEmisorActivo();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('facturacion:ver')")
    public EmisorResponse emisor(@Argument Long id) {
        return facturacionService.buscarEmisorPorId(id);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('facturacion:configurar')")
    public EmisorResponse crearEmisor(@Argument EmisorRequest input) {
        return facturacionService.crearEmisor(input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('facturacion:configurar')")
    public EmisorResponse editarEmisor(@Argument Long id, @Argument EmisorRequest input) {
        return facturacionService.editarEmisor(id, input);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('facturacion:ver')")
    public List<SerieResponse> series() {
        return facturacionService.listarSeries();
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('facturacion:configurar')")
    public SerieResponse crearSerie(@Argument SerieRequest input) {
        return facturacionService.crearSerie(input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('facturacion:emitir')")
    public FacturaResponse generarFactura(@Argument FacturaRequest input) {
        return facturacionService.generarFactura(input);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('facturacion:ver')")
    public FacturaResponse factura(@Argument Long id) {
        return facturacionService.buscarFacturaPorId(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('facturacion:ver')")
    public FacturaResponse facturaPorNumero(@Argument String serie, @Argument Integer correlativo) {
        return facturacionService.buscarFacturaPorNumero(serie, correlativo);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('facturacion:ver')")
    public List<FacturaResponse> facturasPorReserva(@Argument Long reservaId) {
        return facturacionService.listarFacturasPorReserva(reservaId);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('facturacion:ver')")
    public List<FacturaResponse> facturasPorHuesped(@Argument Long huespedId) {
        return facturacionService.listarFacturasPorHuesped(huespedId);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('facturacion:ver')")
    public FacturaPage facturasPaginadas(
            @Argument int page,
            @Argument int size,
            @Argument TipoComprobante tipo,
            @Argument EstadoFactura estado,
            @Argument String fechaInicio,
            @Argument String fechaFin
    ) {
        LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : null;
        LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : null;
        return facturacionService.listarFacturasPaginadas(page, size, tipo, estado, inicio, fin);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('facturacion:ver')")
    public List<DetalleResponse> detalleFactura(@Argument Long facturaId) {
        return facturacionService.listarDetalleFactura(facturaId);
    }
}
