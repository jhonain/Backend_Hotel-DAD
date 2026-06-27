package com.hotel.backend_hotel.Auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
    @NotBlank (message = "el username es obligarorio")
    String username,
    @NotBlank (message = "la contraseña es obligatoria")
    String password
){}
