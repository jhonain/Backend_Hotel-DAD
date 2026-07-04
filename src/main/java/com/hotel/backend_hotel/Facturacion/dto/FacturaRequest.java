package com.hotel.backend_hotel.Facturacion.dto;

import com.hotel.backend_hotel.Enums.TipoComprobante;

import java.util.List;

public record FacturaRequest(
        TipoComprobante tipoComprobante,
        Long clienteId,
        List<Long> reservaIds,
        List<ItemManualInput> itemsExtra,
        String clienteTipoDoc,
        String clienteNumeroDoc,
        String clienteRazonSocial,
        String clienteDireccion
) {}
