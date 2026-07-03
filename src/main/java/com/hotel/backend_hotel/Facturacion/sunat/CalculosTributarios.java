package com.hotel.backend_hotel.Facturacion.sunat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculosTributarios {

    public static final BigDecimal IGV_TASA = new BigDecimal("0.18");
    public static final BigDecimal IGV_FACTOR = new BigDecimal("1.18");

    public static record LineaCalculada(
            BigDecimal valorUnitario,
            BigDecimal precioUnitario,
            BigDecimal valorTotal,
            BigDecimal igv,
            BigDecimal porcentajeIgv,
            BigDecimal importeTotal
    ) {}

    public static LineaCalculada calcularLineaDetalle(double cantidad, double valorUnitarioSinIgv) {
        BigDecimal cant = BigDecimal.valueOf(cantidad);
        BigDecimal vUnit = BigDecimal.valueOf(valorUnitarioSinIgv);

        BigDecimal valorTotal = cant.multiply(vUnit).setScale(2, RoundingMode.HALF_UP);
        BigDecimal igvLinea = valorTotal.multiply(IGV_TASA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal precioUnit = vUnit.multiply(IGV_FACTOR).setScale(2, RoundingMode.HALF_UP);
        BigDecimal importeTotal = valorTotal.add(igvLinea).setScale(2, RoundingMode.HALF_UP);

        return new LineaCalculada(vUnit, precioUnit, valorTotal, igvLinea, IGV_TASA, importeTotal);
    }

    public static record TotalesCalculados(
            BigDecimal opGravadas,
            BigDecimal igv,
            BigDecimal total
    ) {}

    public static TotalesCalculados calcularTotalesDesdeTotalConIgv(double totalConIgv) {
        BigDecimal total = BigDecimal.valueOf(totalConIgv).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        BigDecimal opGravadas = total.divide(IGV_FACTOR, 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(opGravadas).setScale(2, RoundingMode.HALF_UP);
        return new TotalesCalculados(opGravadas, igv, total);
    }

    public static TotalesCalculados sumarTotalesLineas(java.util.List<LineaCalculada> lineas) {
        BigDecimal op = BigDecimal.ZERO;
        BigDecimal igv = BigDecimal.ZERO;
        for (LineaCalculada linea : lineas) {
            op = op.add(linea.valorTotal());
            igv = igv.add(linea.igv());
        }
        op = op.setScale(2, RoundingMode.HALF_UP);
        igv = igv.setScale(2, RoundingMode.HALF_UP);
        return new TotalesCalculados(op, igv, op.add(igv).setScale(2, RoundingMode.HALF_UP));
    }
}
