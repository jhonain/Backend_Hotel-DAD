package com.hotel.backend_hotel.Huesped.service;

import com.hotel.backend_hotel.Huesped.dto.HuespedPage;
import com.hotel.backend_hotel.Huesped.dto.HuespedRequest;
import com.hotel.backend_hotel.Huesped.dto.HuespedResponse;

import java.util.List;

public interface HuespedService {
    List<HuespedResponse> listarHuespedes();
    HuespedPage listarHuespedesPaginados(int page, int size);
    HuespedResponse buscarPorId(Long id);
    HuespedResponse buscarPorDocumento(String numeroDocumento);
    HuespedResponse crearHuesped(HuespedRequest request);
    HuespedResponse editarHuesped(Long id, HuespedRequest request);
    void eliminarHuesped(Long id);
}
