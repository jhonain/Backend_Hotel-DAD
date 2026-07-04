package com.hotel.backend_hotel.Facturacion.dto;

public record ItemManualInput(
        String descripcion,
        Double cantidad,
        Double precioUnitario,
        String unidadMedida
) {}
