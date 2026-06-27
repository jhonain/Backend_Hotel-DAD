package com.hotel.backend_hotel.Empleado.entity;


import com.hotel.backend_hotel.Enums.Cargo;
import com.hotel.backend_hotel.Enums.TurnoEmpleado;
import com.hotel.backend_hotel.Auth.entity.Usuario;
import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "empleados")
public class Empleado extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;

    @Column(nullable = false)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TurnoEmpleado turno;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    private Double salario;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario; // Cada empleado tiene un usuario del sistema

    // ========== CÁLCULOS DE PAGO MENSUAL ==========

    @Transient
    public LocalDate getProximoPago() {
        LocalDate hoy = LocalDate.now();
        int diaContratacion = this.fechaContratacion.getDayOfMonth();

        // Primer pago: mes siguiente a la contratación
        LocalDate primerPago = this.fechaContratacion.plusMonths(1);

        if (hoy.isBefore(primerPago)) {
            return primerPago;
        }

        // Calcular próximo pago: mismo día del mes siguiente
        LocalDate proximo = hoy.withDayOfMonth(1).plusMonths(1).withDayOfMonth(diaContratacion);

        // Ajustar si el día no existe en ese mes (ej: 31 de febrero)
        if (proximo.getDayOfMonth() != diaContratacion) {
            proximo = proximo.withDayOfMonth(proximo.lengthOfMonth());
        }

        return proximo;
    }

    @Transient
    public Long getDiasParaPago() {
        return ChronoUnit.DAYS.between(LocalDate.now(), getProximoPago());
    }

    @Transient
    public Boolean getPagoPendiente() {
        return getDiasParaPago() <= 0;
    }

    @Transient
    public String getEstadoPago() {
        Long dias = getDiasParaPago();
        if (dias < 0) return "ATRASADO";
        if (dias == 0) return "HOY";
        if (dias <= 3) return "PROXIMO";
        return "AL_DIA";
    }

    @Transient
    public Double getSalarioPendiente() {
        // Si está atrasado, acumula meses pendientes
        if (getEstadoPago().equals("ATRASADO")) {
            long mesesAtrasados = ChronoUnit.MONTHS.between(
                    getProximoPago().withDayOfMonth(1),
                    LocalDate.now().withDayOfMonth(1)
            );
            return this.salario * (mesesAtrasados + 1);
        }
        return this.salario;
    }
}
