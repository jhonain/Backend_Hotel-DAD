package com.hotel.backend_hotel.Caja.repository;

import com.hotel.backend_hotel.Caja.entity.MovimientoCaja;
import com.hotel.backend_hotel.Enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findByCajaId(Long cajaId);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.caja.id = :cajaId AND m.tipo = :tipo")
    Double sumByCajaIdAndTipo(@Param("cajaId") Long cajaId, @Param("tipo") TipoMovimiento tipo);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.tipo = :tipo AND m.fecha BETWEEN :inicio AND :fin")
    Double sumByTipoAndFechaBetween(@Param("tipo") TipoMovimiento tipo,
                                     @Param("inicio") LocalDateTime inicio,
                                     @Param("fin") LocalDateTime fin);
}
