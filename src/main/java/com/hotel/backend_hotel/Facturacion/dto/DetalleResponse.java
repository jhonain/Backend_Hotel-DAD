package com.hotel.backend_hotel.Facturacion.dto;

public record DetalleResponse(
        Long id,
        Integer item,
        String descripcion,
        Double cantidad,
        String unidadMedida,
        Double valorUnitario,
        Double precioUnitario,
        Double igv,
        Double porcentajeIgv,
        Double valorTotal,
        Double importeTotal
) {}
