package com.hotel.backend_hotel.Facturacion.dto;

import com.hotel.backend_hotel.Enums.EstadoFactura;
import com.hotel.backend_hotel.Enums.MetodoPago;
import com.hotel.backend_hotel.Enums.TipoComprobante;

import java.time.LocalDate;

public record FacturaResponse(
        Long id,
        Long emisorId,
        String emisorRuc,
        String emisorRazonSocial,
        Long serieId,
        String serieCodigo,
        Integer correlativo,
        TipoComprobante tipoComprobante,
        String numeroComprobante,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        String clienteTipoDoc,
        String clienteNumeroDoc,
        String clienteRazonSocial,
        Double opGravadas,
        Double igv,
        Double total,
        String nombreXml,
        String codigoSunat,
        String mensajeSunat,
        EstadoFactura estado,
        Long reservaId,
        Long huespedId,
        MetodoPago metodoPago
) {}
