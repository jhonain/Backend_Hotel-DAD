package com.hotel.backend_hotel.Facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EmisorRequest(
        @NotBlank @Size(min = 11, max = 11) String ruc,
        @NotBlank String razonSocial,
        String nombreComercial,
        String direccion,
        String ubigeo,
        String departamento,
        String provincia,
        String distrito,
        String usuarioSol,
        String claveSol,
        BigDecimal porcentajeIgv
) {}
