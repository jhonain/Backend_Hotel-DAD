package com.hotel.backend_hotel.Caja.repository;

import com.hotel.backend_hotel.Caja.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByReservaId(Long reservaId);

    List<Pago> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);
}
