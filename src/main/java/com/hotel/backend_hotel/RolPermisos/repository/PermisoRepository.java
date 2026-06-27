package com.hotel.backend_hotel.RolPermisos.repository;

import com.hotel.backend_hotel.RolPermisos.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermisoRepository extends JpaRepository<Permiso,Long> {

    Optional<Permiso> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
