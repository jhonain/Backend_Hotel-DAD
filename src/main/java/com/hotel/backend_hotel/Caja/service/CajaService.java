package com.hotel.backend_hotel.Caja.service;

import com.hotel.backend_hotel.Caja.dto.*;

import java.util.List;

public interface CajaService {
    CajaResponse abrirCaja(Double montoInicial);
    CajaResponse cerrarCaja(Long cajaId, Double montoFinal);
    CajaResponse buscarPorId(Long id);
    CajaResponse buscarCajaAbierta();
    List<CajaResponse> listarPorFecha(String inicio, String fin);
    List<CajaResponse> listarTodas();
    CajaPage listarPorFechaPaginado(String inicio, String fin, int page, int size);

    MovimientoResponse registrarIngreso(Long cajaId, Long reservaId, Double monto, String concepto);
    MovimientoResponse registrarEgreso(MovimientoRequest request);
    List<MovimientoResponse> movimientosDeCaja(Long cajaId);
    MovimientoPage movimientosDeCajaPaginados(Long cajaId, int page, int size);
    ResumenCaja resumenCaja(String inicio, String fin);
}
