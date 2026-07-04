package com.hotel.backend_hotel.Reserva.repository;

import com.hotel.backend_hotel.Enums.EstadoReserva;
import com.hotel.backend_hotel.Reserva.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByHuespedId(Long huespedId);

    List<Reserva> findByHabitacionId(Long habitacionId);

    List<Reserva> findByEstado(EstadoReserva estado);

    List<Reserva> findByEstadoAndCheckOutBefore(EstadoReserva estado, LocalDateTime fecha);

    List<Reserva> findByEstadoAndCheckInBefore(EstadoReserva estado, LocalDateTime fecha);

    List<Reserva> findByEmpleadoId(Long empleadoId);

    @Query("SELECT r FROM Reserva r WHERE r.empleado.id = :empleadoId " +
           "AND r.checkIn >= :inicio AND r.checkIn <= :fin")
    List<Reserva> findByEmpleadoIdAndFechaBetween(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.empleado.id = :empleadoId " +
           "AND r.checkIn >= :inicio AND r.checkIn <= :fin")
    long countByEmpleadoIdAndFechaBetween(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT r FROM Reserva r WHERE r.habitacion.id = :habitacionId " +
           "AND r.estado = 'PENDIENTE' " +
           "AND r.checkIn < :checkOut AND r.checkOut > :checkIn")
    List<Reserva> findOverlapping(
            @Param("habitacionId") Long habitacionId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut);

    @Query("SELECT r FROM Reserva r WHERE r.huesped.id = :huespedId AND r.estado = 'COMPLETADA' " +
           "AND r.id NOT IN (SELECT f.reserva.id FROM Factura f WHERE f.reserva IS NOT NULL)")
    List<Reserva> findFacturablesByHuespedId(@Param("huespedId") Long huespedId);
}
