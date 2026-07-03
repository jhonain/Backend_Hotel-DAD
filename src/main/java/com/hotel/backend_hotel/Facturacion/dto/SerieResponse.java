package com.hotel.backend_hotel.Facturacion.dto;

import com.hotel.backend_hotel.Enums.TipoComprobante;

public record SerieResponse(
        Long id,
        String serie,
        Integer correlativo,
        TipoComprobante tipoComprobante,
        Boolean activo
) {}
