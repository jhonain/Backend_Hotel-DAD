package com.hotel.backend_hotel.Facturacion.dto;

import com.hotel.backend_hotel.Enums.TipoComprobante;
import jakarta.validation.constraints.NotNull;

public record FacturaRequest(
        @NotNull TipoComprobante tipoComprobante,
        @NotNull Long reservaId,
        String clienteTipoDoc,
        String clienteNumeroDoc,
        String clienteRazonSocial,
        String clienteDireccion
) {}
