package com.hotel.backend_hotel.Facturacion.service;

import com.hotel.backend_hotel.Enums.EstadoFactura;
import com.hotel.backend_hotel.Enums.TipoComprobante;
import com.hotel.backend_hotel.Facturacion.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface FacturacionService {

    EmisorResponse crearEmisor(EmisorRequest request);
    EmisorResponse editarEmisor(Long id, EmisorRequest request);
    EmisorResponse buscarEmisorPorId(Long id);
    EmisorResponse buscarEmisorActivo();

    SerieResponse crearSerie(SerieRequest request);
    List<SerieResponse> listarSeries();
    SerieResponse buscarSeriePorTipo(TipoComprobante tipo);

    FacturaResponse generarFactura(FacturaRequest request);
    FacturaResponse buscarFacturaPorId(Long id);
    FacturaResponse buscarFacturaPorNumero(String serie, Integer correlativo);
    List<FacturaResponse> listarFacturasPorReserva(Long reservaId);
    List<FacturaResponse> listarFacturasPorHuesped(Long huespedId);
    List<DetalleResponse> listarDetalleFactura(Long facturaId);

    FacturaPage listarFacturasPaginadas(int page, int size, TipoComprobante tipo, EstadoFactura estado, LocalDate fechaInicio, LocalDate fechaFin);
}
