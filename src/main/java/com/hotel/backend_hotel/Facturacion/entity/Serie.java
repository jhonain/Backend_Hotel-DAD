package com.hotel.backend_hotel.Facturacion.entity;

import com.hotel.backend_hotel.Enums.TipoComprobante;
import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "series")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Serie extends BaseEntity {

    @Column(nullable = false, length = 4)
    private String serie;

    @Column(nullable = false)
    private Integer correlativo = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    private TipoComprobante tipoComprobante;

    @Column(name = "activo")
    private Boolean activo = true;

    @Version
    private Integer version;
}
