package com.hotel.backend_hotel.Caja.entity;

import com.hotel.backend_hotel.Empleado.entity.Empleado;
import com.hotel.backend_hotel.Enums.EstadoCaja;
import com.hotel.backend_hotel.Enums.TurnoEmpleado;
import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cajas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Caja extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "monto_inicial", nullable = false)
    private Double montoInicial = 0.0;

    @Column(name = "monto_final")
    private Double montoFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCaja estado = EstadoCaja.ABIERTA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TurnoEmpleado turno;
}
