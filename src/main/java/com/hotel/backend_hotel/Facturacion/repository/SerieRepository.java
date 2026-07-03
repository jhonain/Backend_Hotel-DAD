package com.hotel.backend_hotel.Facturacion.repository;

import com.hotel.backend_hotel.Enums.TipoComprobante;
import com.hotel.backend_hotel.Facturacion.entity.Serie;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    Optional<Serie> findByTipoComprobanteAndActivoTrue(TipoComprobante tipoComprobante);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Serie s WHERE s.id = :id")
    Optional<Serie> findByIdWithLock(@Param("id") Long id);
}
