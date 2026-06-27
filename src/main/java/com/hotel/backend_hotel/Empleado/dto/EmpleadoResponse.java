package com.hotel.backend_hotel.Empleado.dto;

public record EmpleadoResponse(
        Long id,
        String nombre,
        String apellido,
        String cargo,
        String telefono,
        String turno,
        Boolean activo,
        String fechaContratacion,
        Double salario,

        // Cálculos de pago
        String proximoPago,
        Long diasParaPago,
        String estadoPago,
        Boolean pagoPendiente,
        Double salarioPendiente,

        UsuarioInfo usuario
) {
    public record UsuarioInfo(Long id, String username, String email) {}
}