package com.hotel.backend_hotel.ServiciosAdicionales.repository;

import com.hotel.backend_hotel.ServiciosAdicionales.entity.ServicioAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioAdicionalRepository extends JpaRepository<ServicioAdicional, Long> {
    List<ServicioAdicional> findByCategoria(String categoria);
    List<ServicioAdicional> findByDisponibleTrue();
    List<ServicioAdicional> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
