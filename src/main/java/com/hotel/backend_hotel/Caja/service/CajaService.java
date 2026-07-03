package com.hotel.backend_hotel.Caja.service;

import com.hotel.backend_hotel.Caja.dto.*;
import com.hotel.backend_hotel.Reserva.entity.Reserva;

import java.util.List;

public interface CajaService {
    CajaResponse abrirCaja(Long empleadoId, Double montoInicial);
    CajaResponse cerrarCaja(Long cajaId, Double montoFinal);
    CajaResponse obtenerOCrearCajaAbierta(Long empleadoId);
    CajaResponse buscarPorId(Long id);
    CajaResponse buscarCajaAbierta(Long empleadoId);
    List<CajaResponse> listarPorEmpleado(Long empleadoId);
    List<CajaResponse> listarPorFecha(String inicio, String fin);
    List<CajaResponse> listarTodas();
    CajaPage listarPorFechaPaginado(String inicio, String fin, int page, int size);

    MovimientoResponse registrarIngreso(Long cajaId, Long reservaId, Double monto, String concepto);
    MovimientoResponse registrarEgreso(MovimientoRequest request);
    List<MovimientoResponse> movimientosDeCaja(Long cajaId);
    MovimientoPage movimientosDeCajaPaginados(Long cajaId, int page, int size);
    ResumenCaja resumenCaja(String inicio, String fin);
}
