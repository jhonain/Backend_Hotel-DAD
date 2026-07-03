package com.hotel.backend_hotel.Facturacion.repository;

import com.hotel.backend_hotel.Facturacion.entity.Emisor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmisorRepository extends JpaRepository<Emisor, Long> {
    Optional<Emisor> findByRuc(String ruc);
    Optional<Emisor> findByActivoTrue();
}
