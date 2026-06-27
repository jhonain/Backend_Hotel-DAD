package com.hotel.backend_hotel.Auth.service.Impl;

import com.hotel.backend_hotel.Auth.dto.LoginRequest;
import com.hotel.backend_hotel.Auth.dto.LoginResponse;
import com.hotel.backend_hotel.Auth.dto.RegisterRequest;
import com.hotel.backend_hotel.Auth.entity.Usuario;
import com.hotel.backend_hotel.Auth.repository.UserRepository;
import com.hotel.backend_hotel.Auth.service.AuthService;
import com.hotel.backend_hotel.Security.JwtUtils;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. Autenticar con Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        // 2. Buscar el usuario (findByUsername, no ExisteUsuario)
        Usuario user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Generar token (Usuario ya es UserDetails)
        String token = jwtUtils.generateToken(user);

        return new LoginResponse(token, user.getUsername(), user.getEmail());
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        // Validar que no exista
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("El username ya está registrado");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear usuario
        Usuario user = new Usuario();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(true);

        userRepository.save(user);

        // Generar token y devolver
        String token = jwtUtils.generateToken(user);
        return new LoginResponse(token, user.getUsername(), user.getEmail());
    }

    @Override
    @Transactional
    public void EliminarUsuario(Long id) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Usuario no encontrado"));

        // Limpiar relaciones (opcional pero recomendado)
        usuario.getRoles().clear();

        userRepository.delete(usuario);
    }
}