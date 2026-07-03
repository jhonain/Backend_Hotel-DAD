package com.hotel.backend_hotel.Facturacion.service;

import com.hotel.backend_hotel.Facturacion.entity.Serie;
import com.hotel.backend_hotel.Facturacion.repository.SerieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NumeracionService {

    private final SerieRepository serieRepository;

    @Transactional
    public synchronized int siguienteCorrelativo(Long serieId) {
        Serie serie = serieRepository.findByIdWithLock(serieId)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada: " + serieId));
        int nuevo = serie.getCorrelativo() + 1;
        serie.setCorrelativo(nuevo);
        serieRepository.save(serie);
        return nuevo;
    }

    public String formatoNumeracion(String serie, int correlativo) {
        return serie + "-" + String.format("%08d", correlativo);
    }

    public String nombreArchivoSunat(String ruc, String tipo, String serie, int correlativo) {
        return ruc + "-" + tipo + "-" + serie + "-" + String.format("%08d", correlativo);
    }
}
