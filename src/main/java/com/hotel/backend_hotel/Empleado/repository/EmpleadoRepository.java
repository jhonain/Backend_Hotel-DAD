package com.hotel.backend_hotel.Empleado.repository;

import com.hotel.backend_hotel.Empleado.entity.Empleado;
import com.hotel.backend_hotel.Enums.Cargo;
import com.hotel.backend_hotel.Enums.TurnoEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findByCargo(Cargo cargo);

    List<Empleado> findByTurno(TurnoEmpleado turno);

    List<Empleado> findByActivo(Boolean activo);

    boolean existsByUsuarioId(Long usuarioId);

}
