package com.hotel.backend_hotel.Facturacion.repository;

import com.hotel.backend_hotel.Enums.EstadoFactura;
import com.hotel.backend_hotel.Enums.TipoComprobante;
import com.hotel.backend_hotel.Facturacion.entity.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT f FROM Factura f WHERE " +
           "(:tipo IS NULL OR f.tipoComprobante = :tipo) AND " +
           "(:estado IS NULL OR f.estado = :estado) AND " +
           "f.fechaEmision >= :fechaInicio AND f.fechaEmision <= :fechaFin")
    Page<Factura> findAllWithFilters(
            @Param("tipo") TipoComprobante tipo,
            @Param("estado") EstadoFactura estado,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable);
}
