package com.hotel.backend_hotel.Facturacion.repository;

import com.hotel.backend_hotel.Facturacion.entity.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {
    List<DetalleFactura> findByFacturaId(Long facturaId);
}
