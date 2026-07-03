package com.hotel.backend_hotel.Facturacion.entity;

import com.hotel.backend_hotel.Enums.EstadoFactura;
import com.hotel.backend_hotel.Enums.MetodoPago;
import com.hotel.backend_hotel.Enums.TipoComprobante;
import com.hotel.backend_hotel.Huesped.entity.Huesped;
import com.hotel.backend_hotel.Reserva.entity.Reserva;
import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_id", nullable = false)
    private Emisor emisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    private TipoComprobante tipoComprobante;

    @Column(nullable = false, length = 4)
    private String serieCodigo;

    @Column(nullable = false)
    private Integer correlativo;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "cliente_tipo_doc", length = 2)
    private String clienteTipoDoc;

    @Column(name = "cliente_numero_doc", length = 20)
    private String clienteNumeroDoc;

    @Column(name = "cliente_razon_social", length = 100)
    private String clienteRazonSocial;

    @Column(name = "cliente_direccion", length = 100)
    private String clienteDireccion;

    @Column(name = "op_gravadas", nullable = false)
    private Double opGravadas = 0.0;

    @Column(name = "op_exoneradas")
    private Double opExoneradas = 0.0;

    @Column(name = "op_inafectas")
    private Double opInafectas = 0.0;

    @Column(nullable = false)
    private Double igv = 0.0;

    @Column(nullable = false)
    private Double total = 0.0;

    @Column(name = "nombre_xml", length = 100)
    private String nombreXml;

    @Column(name = "xml_firmado", columnDefinition = "TEXT")
    private String xmlFirmado;

    @Column(name = "cdr_base64", columnDefinition = "TEXT")
    private String cdrBase64;

    @Column(length = 20)
    private String codigoSunat;

    @Column(length = 500)
    private String mensajeSunat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFactura estado = EstadoFactura.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huesped_id")
    private Huesped huesped;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago;
}
