package com.hotel.backend_hotel.Facturacion.repository;

import com.hotel.backend_hotel.Enums.EstadoFactura;
import com.hotel.backend_hotel.Facturacion.entity.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    Optional<Factura> findBySerieCodigoAndCorrelativo(String serieCodigo, Integer correlativo);

    List<Factura> findByReservaId(Long reservaId);

    List<Factura> findByHuespedId(Long huespedId);

    Page<Factura> findByEstado(EstadoFactura estado, Pageable pageable);

    Page<Factura> findByFechaEmisionBetween(LocalDate inicio, LocalDate fin, Pageable pageable);

    List<Factura> findByEstadoAndCodigoSunatIsNull(EstadoFactura estado);

    long countByEmisorIdAndFechaEmisionBetween(Long emisorId, LocalDate inicio, LocalDate fin);
}
