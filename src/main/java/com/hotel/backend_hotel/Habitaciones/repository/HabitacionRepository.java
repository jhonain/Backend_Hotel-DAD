package com.hotel.backend_hotel.Habitaciones.repository;

import com.hotel.backend_hotel.Enums.EstadoHabitacion;
import com.hotel.backend_hotel.Habitaciones.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    List<Habitacion> findByEstado(EstadoHabitacion estado);
    List<Habitacion> findByTipo(String tipo);
    List<Habitacion> findByPiso(Integer piso);
    List<Habitacion> findByCapacidadGreaterThanEqual(Integer capacidad);
    List<Habitacion> findByPrecioBetween(Double min, Double max);
    Optional<Habitacion> findByNumero(String numero);

    long countByEstado(EstadoHabitacion estado);
}
