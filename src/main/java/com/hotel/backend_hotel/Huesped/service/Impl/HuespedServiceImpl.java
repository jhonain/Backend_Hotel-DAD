package com.hotel.backend_hotel.Huesped.service.Impl;

import com.hotel.backend_hotel.Huesped.dto.HuespedPage;
import com.hotel.backend_hotel.Huesped.dto.HuespedRequest;
import com.hotel.backend_hotel.Huesped.dto.HuespedResponse;
import com.hotel.backend_hotel.Huesped.entity.Huesped;
import com.hotel.backend_hotel.Huesped.repository.HuespedRepository;
import com.hotel.backend_hotel.Huesped.service.HuespedService;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionEmpresarial;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HuespedServiceImpl implements HuespedService {

    private final HuespedRepository huespedRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HuespedResponse> listarHuespedes() {
        return huespedRepository.findAll().stream()
                .map(this::toHuespedResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HuespedPage listarHuespedesPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        Page<Huesped> pageResult = huespedRepository.findAll(pageable);

        List<HuespedResponse> items = pageResult.getContent().stream()
                .map(this::toHuespedResponse)
                .toList();

        return new HuespedPage(items, pageResult.getTotalElements(),
                pageResult.getTotalPages(), pageResult.getNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public HuespedResponse buscarPorId(Long id) {
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Huésped no encontrado con id: " + id));
        return toHuespedResponse(huesped);
    }

    @Override
    @Transactional(readOnly = true)
    public HuespedResponse buscarPorDocumento(String numeroDocumento) {
        Huesped huesped = huespedRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Huésped no encontrado con documento: " + numeroDocumento));
        return toHuespedResponse(huesped);
    }

    @Override
    @Transactional
    public HuespedResponse crearHuesped(HuespedRequest request) {
        if (huespedRepository.existsByNumeroDocumento(request.numeroDocumento())) {
            throw new ExcepcionEmpresarial("Ya existe un huésped con el documento: " + request.numeroDocumento());
        }

        if (request.email() != null && huespedRepository.existsByEmail(request.email())) {
            throw new ExcepcionEmpresarial("Ya existe un huésped con el email: " + request.email());
        }

        Huesped huesped = new Huesped();
        huesped.setNombre(request.nombre());
        huesped.setApellido(request.apellido());
        huesped.setTipoDocumento(request.tipoDocumento());
        huesped.setNumeroDocumento(request.numeroDocumento());
        huesped.setEmail(request.email());
        huesped.setTelefono(request.telefono());
        huesped.setNacionalidad(request.nacionalidad());

        huesped = huespedRepository.save(huesped);
        return toHuespedResponse(huesped);
    }

    @Override
    @Transactional
    public HuespedResponse editarHuesped(Long id, HuespedRequest request) {
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Huésped no encontrado con id: " + id));

        if (!huesped.getNumeroDocumento().equals(request.numeroDocumento()) &&
                huespedRepository.existsByNumeroDocumento(request.numeroDocumento())) {
            throw new ExcepcionEmpresarial("Ya existe otro huésped con el documento: " + request.numeroDocumento());
        }

        if (request.email() != null &&
                !request.email().equals(huesped.getEmail()) &&
                huespedRepository.existsByEmail(request.email())) {
            throw new ExcepcionEmpresarial("Ya existe otro huésped con el email: " + request.email());
        }

        huesped.setNombre(request.nombre());
        huesped.setApellido(request.apellido());
        huesped.setTipoDocumento(request.tipoDocumento());
        huesped.setNumeroDocumento(request.numeroDocumento());
        huesped.setEmail(request.email());
        huesped.setTelefono(request.telefono());
        huesped.setNacionalidad(request.nacionalidad());

        huesped = huespedRepository.save(huesped);
        return toHuespedResponse(huesped);
    }

    @Override
    @Transactional
    public void eliminarHuesped(Long id) {
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Huésped no encontrado con id: " + id));
        huespedRepository.delete(huesped);
    }

    private HuespedResponse toHuespedResponse(Huesped huesped) {
        return new HuespedResponse(
                huesped.getId(),
                huesped.getNombre(),
                huesped.getApellido(),
                huesped.getTipoDocumento().name(),
                huesped.getNumeroDocumento(),
                huesped.getEmail(),
                huesped.getTelefono(),
                huesped.getNacionalidad()
        );
    }
}
