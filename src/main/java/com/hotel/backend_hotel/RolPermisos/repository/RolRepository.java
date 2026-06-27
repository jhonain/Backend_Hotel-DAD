package com.hotel.backend_hotel.RolPermisos.repository;

import com.hotel.backend_hotel.RolPermisos.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol,Long> {
    Optional<Rol> findByNombre(String nombre);
}
