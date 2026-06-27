package com.hotel.backend_hotel.Auth.service;

import com.hotel.backend_hotel.Auth.dto.LoginRequest;
import com.hotel.backend_hotel.Auth.dto.LoginResponse;
import com.hotel.backend_hotel.Auth.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse register(RegisterRequest registerRequest);
    void EliminarUsuario(Long id);
}
