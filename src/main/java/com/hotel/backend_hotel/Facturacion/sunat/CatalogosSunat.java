package com.hotel.backend_hotel.Facturacion.sunat;

import com.hotel.backend_hotel.Enums.TipoDoc;

import java.util.Set;

public class CatalogosSunat {

    private static final Set<String> AFECTACION_IGV_VALIDAS = Set.of(
            "10", "11", "12", "13", "14", "15", "16", "17",
            "20", "21", "30", "31", "32", "33", "34", "35", "36", "40"
    );

    public static String codigoAfectacionIgv(String codigo) {
        String c = (codigo == null || codigo.isBlank()) ? "10" : codigo.trim();
        if (AFECTACION_IGV_VALIDAS.contains(c)) return c;
        if ("1000".equals(c)) return "10";
        if (c.length() == 4 && c.startsWith("10") && c.chars().allMatch(Character::isDigit)) return "10";
        return "10";
    }

    public static String tipoDocToSunat(TipoDoc tipoDoc) {
        if (tipoDoc == null) return "1";
        return switch (tipoDoc) {
            case DNI -> "1";
            case CE -> "4";
            case PASAPORTE -> "7";
            case RUC -> "6";
        };
    }

    public static boolean puedeEmitirFactura(TipoDoc tipoDoc) {
        return tipoDoc == TipoDoc.RUC;
    }

    public static String detectarTipoDoc(String numeroDoc) {
        if (numeroDoc == null || numeroDoc.isBlank()) return "1";
        String limpio = numeroDoc.trim();
        if (limpio.length() == 11) return "6";
        return "1";
    }

    public static String tipoDocFromSunat(String codigo) {
        if (codigo == null) return "1";
        return switch (codigo) {
            case "6" -> "6";
            case "4" -> "4";
            case "7" -> "7";
            default -> "1";
        };
    }
}
