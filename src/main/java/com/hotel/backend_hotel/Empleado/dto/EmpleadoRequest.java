package com.hotel.backend_hotel.Empleado.dto;

import com.hotel.backend_hotel.Enums.Cargo;
import com.hotel.backend_hotel.Enums.TurnoEmpleado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmpleadoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotNull(message = "El cargo es obligatorio")
        Cargo cargo,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono,

        @NotNull(message = "El turno es obligatorio")
        TurnoEmpleado turno,

        @NotNull(message = "El salario es obligatorio")
        Double salario,

        String fechaContratacion   // "2026-06-05"
) {
}