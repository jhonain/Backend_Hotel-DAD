package com.hotel.backend_hotel.Huesped.repository;

import com.hotel.backend_hotel.Huesped.entity.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HuespedRepository extends JpaRepository<Huesped, Long> {
    Optional<Huesped> findByNumeroDocumento(String numeroDocumento);
    Optional<Huesped> findByEmail(String email);
    List<Huesped> findByNacionalidad(String nacionalidad);
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByEmail(String email);
}
