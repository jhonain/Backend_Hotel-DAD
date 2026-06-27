package com.hotel.backend_hotel.Auth.dto;

public record LoginResponse(
        String token,
        String username,
        String email
) {
}
