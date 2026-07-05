package com.hotel.backend_hotel.Facturacion.entity;

import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalles_factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFactura extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @Column(nullable = false)
    private Integer item;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Double cantidad;

    @Column(name = "unidad_medida", length = 4)
    private String unidadMedida = "ZZ";

    @Column(name = "valor_unitario", nullable = false)
    private Double valorUnitario;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Double igv;

    @Column(name = "porcentaje_igv")
    private Double porcentajeIgv = 10.5;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;

    @Column(name = "importe_total", nullable = false)
    private Double importeTotal;
}
