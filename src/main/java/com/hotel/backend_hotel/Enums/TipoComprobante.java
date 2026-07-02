package com.hotel.backend_hotel.Enums;

public enum TipoComprobante {
    FACTURA("01"),
    BOLETA("03");

    private final String codigoSunat;

    TipoComprobante(String codigoSunat) {
        this.codigoSunat = codigoSunat;
    }

    public String getCodigoSunat() {
        return codigoSunat;
    }

    public static TipoComprobante fromCodigo(String codigo) {
        for (TipoComprobante t : values()) {
            if (t.codigoSunat.equals(codigo)) return t;
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }
}
