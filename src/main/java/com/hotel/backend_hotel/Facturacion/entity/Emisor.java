package com.hotel.backend_hotel.Facturacion.entity;

import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "emisores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emisor extends BaseEntity {

    @Column(nullable = false, length = 11)
    private String ruc;

    @Column(name = "razon_social", nullable = false, length = 100)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 100)
    private String nombreComercial;

    @Column(length = 100)
    private String direccion;

    @Column(length = 6)
    private String ubigeo;

    @Column(length = 50)
    private String departamento;

    @Column(length = 50)
    private String provincia;

    @Column(length = 50)
    private String distrito;

    @Column(name = "usuario_sol", length = 20)
    private String usuarioSol;

    @Column(name = "clave_sol", length = 20)
    private String claveSol;

    @Column(name = "porcentaje_igv", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeIgv = new BigDecimal("10.50");

    @Column(name = "activo")
    private Boolean activo = true;
}
