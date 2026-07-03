package com.hotel.backend_hotel.ServiciosAdicionales.entity;

import com.hotel.backend_hotel.Enums.CategoriaServicio;
import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "servicios_adicionales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioAdicional extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaServicio categoria;

    @Column(nullable = false)
    private Boolean disponible = true;
}
