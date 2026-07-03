package com.hotel.backend_hotel.Facturacion.dto;

import java.math.BigDecimal;

public record EmisorResponse(
        Long id,
        String ruc,
        String razonSocial,
        String nombreComercial,
        String direccion,
        String ubigeo,
        String departamento,
        String provincia,
        String distrito,
        BigDecimal porcentajeIgv,
        Boolean activo
) {}
