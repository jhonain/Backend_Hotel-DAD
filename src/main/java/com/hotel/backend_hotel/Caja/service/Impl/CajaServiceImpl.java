package com.hotel.backend_hotel.Caja.service.Impl;

import com.hotel.backend_hotel.Caja.dto.*;
import com.hotel.backend_hotel.Caja.entity.Caja;
import com.hotel.backend_hotel.Caja.entity.MovimientoCaja;
import com.hotel.backend_hotel.Caja.repository.CajaRepository;
import com.hotel.backend_hotel.Caja.repository.MovimientoRepository;
import com.hotel.backend_hotel.Caja.service.CajaService;
import com.hotel.backend_hotel.Empleado.entity.Empleado;
import com.hotel.backend_hotel.Empleado.repository.EmpleadoRepository;
import com.hotel.backend_hotel.Enums.EstadoCaja;
import com.hotel.backend_hotel.Enums.TipoMovimiento;
import com.hotel.backend_hotel.Enums.TurnoEmpleado;
import com.hotel.backend_hotel.Reserva.entity.Reserva;
import com.hotel.backend_hotel.Reserva.repository.ReservaRepository;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionEmpresarial;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final CajaRepository cajaRepository;
    private final MovimientoRepository movimientoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ReservaRepository reservaRepository;

    @Override
    @Transactional
    public CajaResponse abrirCaja(Long empleadoId, Double montoInicial) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Empleado no encontrado con id: " + empleadoId));

        if (cajaRepository.findByEmpleadoIdAndEstado(empleadoId, EstadoCaja.ABIERTA).isPresent()) {
            throw new ExcepcionEmpresarial("El empleado ya tiene una caja abierta");
        }

        TurnoEmpleado turno = determinarTurno();
        String codigo = generarCodigo();

        Caja caja = new Caja();
        caja.setCodigo(codigo);
        caja.setEmpleado(empleado);
        caja.setFechaApertura(LocalDateTime.now());
        caja.setMontoInicial(montoInicial != null ? montoInicial : 0.0);
        caja.setEstado(EstadoCaja.ABIERTA);
        caja.setTurno(turno);

        caja = cajaRepository.save(caja);
        return toCajaResponse(caja);
    }

    @Override
    @Transactional
    public CajaResponse cerrarCaja(Long cajaId, Double montoFinal) {
        Caja caja = cajaRepository.findById(cajaId)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Caja no encontrada con id: " + cajaId));

        if (caja.getEstado() != EstadoCaja.ABIERTA) {
            throw new ExcepcionEmpresarial("La caja ya está cerrada");
        }

        caja.setEstado(EstadoCaja.CERRADA);
        caja.setFechaCierre(LocalDateTime.now());
        caja.setMontoFinal(montoFinal);

        caja = cajaRepository.save(caja);
        return toCajaResponse(caja);
    }

    @Override
    @Transactional
    public CajaResponse obtenerOCrearCajaAbierta(Long empleadoId) {
        return cajaRepository.findByEmpleadoIdAndEstado(empleadoId, EstadoCaja.ABIERTA)
                .map(this::toCajaResponse)
                .orElseGet(() -> abrirCaja(empleadoId, 0.0));
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponse buscarPorId(Long id) {
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Caja no encontrada con id: " + id));
        return toCajaResponse(caja);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponse buscarCajaAbierta(Long empleadoId) {
        return cajaRepository.findByEmpleadoIdAndEstado(empleadoId, EstadoCaja.ABIERTA)
                .map(this::toCajaResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CajaResponse> listarPorEmpleado(Long empleadoId) {
        return cajaRepository.findByEmpleadoId(empleadoId).stream()
                .map(this::toCajaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CajaResponse> listarPorFecha(String inicio, String fin) {
        LocalDateTime inicioDt = LocalDateTime.parse(inicio, FMT);
        LocalDateTime finDt = LocalDateTime.parse(fin, FMT);
        return cajaRepository.findByFechaAperturaBetween(inicioDt, finDt).stream()
                .map(this::toCajaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CajaResponse> listarTodas() {
        return cajaRepository.findAll().stream()
                .map(this::toCajaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CajaPage listarPorFechaPaginado(String inicio, String fin, int page, int size) {
        LocalDateTime inicioDt = LocalDateTime.parse(inicio, FMT);
        LocalDateTime finDt = LocalDateTime.parse(fin, FMT);
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaApertura").descending());
        Page<Caja> pageResult = cajaRepository.findByFechaAperturaBetween(inicioDt, finDt, pageable);
        List<CajaResponse> items = pageResult.getContent().stream()
                .map(this::toCajaResponse)
                .toList();
        return new CajaPage(items, pageResult.getTotalElements(),
                pageResult.getTotalPages(), pageResult.getNumber());
    }

    @Override
    @Transactional
    public MovimientoResponse registrarIngreso(Long cajaId, Long reservaId, Double monto, String concepto) {
        Caja caja = cajaRepository.findById(cajaId)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Caja no encontrada con id: " + cajaId));

        if (caja.getEstado() != EstadoCaja.ABIERTA) {
            throw new ExcepcionEmpresarial("La caja está cerrada, no se pueden registrar ingresos");
        }

        MovimientoCaja mov = new MovimientoCaja();
        mov.setCaja(caja);
        mov.setTipo(TipoMovimiento.INGRESO);
        mov.setMonto(monto);
        mov.setConcepto(concepto);
        mov.setFecha(LocalDateTime.now());

        if (reservaId != null) {
            Reserva reserva = reservaRepository.findById(reservaId).orElse(null);
            mov.setReserva(reserva);
        }

        mov = movimientoRepository.save(mov);
        return toMovimientoResponse(mov);
    }

    @Override
    @Transactional
    public MovimientoResponse registrarEgreso(MovimientoRequest request) {
        Caja caja = cajaRepository.findById(request.cajaId())
                .orElseThrow(() -> new ExcepcionNoEncontrada("Caja no encontrada con id: " + request.cajaId()));

        if (caja.getEstado() != EstadoCaja.ABIERTA) {
            throw new ExcepcionEmpresarial("La caja está cerrada, no se pueden registrar egresos");
        }

        MovimientoCaja mov = new MovimientoCaja();
        mov.setCaja(caja);
        mov.setTipo(TipoMovimiento.EGRESO);
        mov.setMonto(request.monto());
        mov.setConcepto(request.concepto());
        mov.setFecha(LocalDateTime.now());

        mov = movimientoRepository.save(mov);
        return toMovimientoResponse(mov);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> movimientosDeCaja(Long cajaId) {
        return movimientoRepository.findByCajaId(cajaId).stream()
                .map(this::toMovimientoResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoPage movimientosDeCajaPaginados(Long cajaId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        Page<MovimientoCaja> pageResult = movimientoRepository.findByCajaId(cajaId, pageable);
        List<MovimientoResponse> items = pageResult.getContent().stream()
                .map(this::toMovimientoResponse)
                .toList();
        return new MovimientoPage(items, pageResult.getTotalElements(),
                pageResult.getTotalPages(), pageResult.getNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenCaja resumenCaja(String inicio, String fin) {
        LocalDateTime inicioDt = inicio != null ? LocalDateTime.parse(inicio, FMT) : LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime finDt = fin != null ? LocalDateTime.parse(fin, FMT) : LocalDateTime.now().with(LocalTime.MAX);

        List<Caja> cajas = cajaRepository.findByFechaAperturaBetween(inicioDt, finDt);
        double totalIngresos = movimientoRepository.sumByTipoAndFechaBetween(TipoMovimiento.INGRESO, inicioDt, finDt);
        double totalEgresos = movimientoRepository.sumByTipoAndFechaBetween(TipoMovimiento.EGRESO, inicioDt, finDt);

        return new ResumenCaja(
                cajas.size(),
                cajas.stream().filter(c -> c.getEstado() == EstadoCaja.ABIERTA).count(),
                totalIngresos,
                totalEgresos,
                totalIngresos - totalEgresos,
                cajas.stream().map(this::toCajaResponse).toList()
        );
    }

    private String generarCodigo() {
        String ultimo = cajaRepository.findMaxCodigo();
        if (ultimo == null) return "CAJA-00001";
        int numero = Integer.parseInt(ultimo.split("-")[1]) + 1;
        return String.format("CAJA-%05d", numero);
    }

    private TurnoEmpleado determinarTurno() {
        LocalTime ahora = LocalTime.now();
        if (ahora.isAfter(LocalTime.of(6, 59)) && ahora.isBefore(LocalTime.of(19, 0))) {
            return TurnoEmpleado.DIA;
        }
        return TurnoEmpleado.NOCHE;
    }

    private CajaResponse toCajaResponse(Caja caja) {
        Double totalIngresos = movimientoRepository.sumByCajaIdAndTipo(caja.getId(), TipoMovimiento.INGRESO);
        Double totalEgresos = movimientoRepository.sumByCajaIdAndTipo(caja.getId(), TipoMovimiento.EGRESO);

        return new CajaResponse(
                caja.getId(),
                caja.getCodigo(),
                caja.getEmpleado().getId(),
                caja.getEmpleado().getNombre() + " " + caja.getEmpleado().getApellido(),
                caja.getFechaApertura().toString(),
                caja.getFechaCierre() != null ? caja.getFechaCierre().toString() : null,
                caja.getMontoInicial(),
                caja.getMontoFinal(),
                totalIngresos,
                totalEgresos,
                caja.getEstado(),
                caja.getTurno()
        );
    }

    private MovimientoResponse toMovimientoResponse(MovimientoCaja mov) {
        return new MovimientoResponse(
                mov.getId(),
                mov.getCaja().getId(),
                mov.getReserva() != null ? mov.getReserva().getId() : null,
                mov.getTipo(),
                mov.getMonto(),
                mov.getConcepto(),
                mov.getFecha().toString()
        );
    }
}
