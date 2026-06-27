package com.hotel.backend_hotel.Habitaciones.entity;

import com.hotel.backend_hotel.Enums.EstadoHabitacion;
import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "habitaciones")
public class Habitacion extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private Integer piso;

    @Column(nullable = false)
    private String tipo; // Simple, Doble, Suite, Matrimonial

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false, name = "precio")
    private Double precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHabitacion estado = EstadoHabitacion.DISPONIBLE;

    private String descripcion;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "limpieza_inicio")
    private LocalDateTime limpiezaInicio;
}