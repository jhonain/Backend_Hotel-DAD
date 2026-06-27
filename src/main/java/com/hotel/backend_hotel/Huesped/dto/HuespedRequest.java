package com.hotel.backend_hotel.Huesped.dto;

import com.hotel.backend_hotel.Enums.TipoDoc;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record HuespedRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotNull(message = "El tipo de documento es obligatorio")
        TipoDoc tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        String numeroDocumento,

        @Email(message = "El email no tiene un formato válido")
        String email,

        @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
        String telefono,

        @NotBlank(message = "La nacionalidad es obligatoria")
        String nacionalidad
) {
}
