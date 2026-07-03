package com.hotel.backend_hotel.Caja.repository;

import com.hotel.backend_hotel.Caja.entity.Caja;
import com.hotel.backend_hotel.Enums.EstadoCaja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CajaRepository extends JpaRepository<Caja, Long> {
    Optional<Caja> findByEmpleadoIdAndEstado(Long empleadoId, EstadoCaja estado);

    List<Caja> findByEmpleadoId(Long empleadoId);

    List<Caja> findByFechaAperturaBetween(LocalDateTime inicio, LocalDateTime fin);

    Page<Caja> findByFechaAperturaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    @Query("SELECT MAX(c.codigo) FROM Caja c")
    String findMaxCodigo();
}
