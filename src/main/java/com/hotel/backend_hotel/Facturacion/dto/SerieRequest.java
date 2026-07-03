package com.hotel.backend_hotel.Facturacion.dto;

import com.hotel.backend_hotel.Enums.TipoComprobante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SerieRequest(
        @NotBlank String serie,
        @NotNull TipoComprobante tipoComprobante
) {}
