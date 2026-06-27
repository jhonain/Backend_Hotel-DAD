package com.hotel.backend_hotel.Reserva.service.Impl;

import com.hotel.backend_hotel.Empleado.entity.Empleado;
import com.hotel.backend_hotel.Empleado.repository.EmpleadoRepository;
import com.hotel.backend_hotel.Enums.EstadoHabitacion;
import com.hotel.backend_hotel.Enums.EstadoReserva;
import com.hotel.backend_hotel.Habitaciones.entity.Habitacion;
import com.hotel.backend_hotel.Habitaciones.repository.HabitacionRepository;
import com.hotel.backend_hotel.Huesped.entity.Huesped;
import com.hotel.backend_hotel.Huesped.repository.HuespedRepository;
import com.hotel.backend_hotel.Reserva.dto.ReservaPage;
import com.hotel.backend_hotel.Reserva.dto.ReservaRequest;
import com.hotel.backend_hotel.Reserva.dto.ReservaResponse;
import com.hotel.backend_hotel.Reserva.entity.Reserva;
import com.hotel.backend_hotel.Reserva.repository.ReservaRepository;
import com.hotel.backend_hotel.Reserva.service.ReservaService;
import com.hotel.backend_hotel.Caja.entity.Pago;
import com.hotel.backend_hotel.Caja.repository.PagoRepository;
import com.hotel.backend_hotel.Caja.service.CajaService;
import com.hotel.backend_hotel.Enums.MetodoPago;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionEmpresarial;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import com.hotel.backend_hotel.common.RealTime.NotificacionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final ReservaRepository reservaRepository;
    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CajaService cajaService;
    private final PagoRepository pagoRepository;
    private final NotificacionResolver notificacionResolver;

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> listarReservas() {
        return reservaRepository.findAll().stream()
                .map(this::actualizarEstadoSiVencido)
                .map(this::toReservaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaPage listarReservasPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("checkIn").descending());
        Page<Reserva> pageResult = reservaRepository.findAll(pageable);

        List<ReservaResponse> items = pageResult.getContent().stream()
                .map(this::actualizarEstadoSiVencido)
                .map(this::toReservaResponse)
                .toList();

        return new ReservaPage(items, pageResult.getTotalElements(),
                pageResult.getTotalPages(), pageResult.getNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponse buscarPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Reserva no encontrada con id: " + id));
        return toReservaResponse(actualizarEstadoSiVencido(reserva));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> buscarPorHuespedId(Long huespedId) {
        return reservaRepository.findByHuespedId(huespedId).stream()
                .map(this::actualizarEstadoSiVencido)
                .map(this::toReservaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> buscarPorHabitacionId(Long habitacionId) {
        return reservaRepository.findByHabitacionId(habitacionId).stream()
                .map(this::actualizarEstadoSiVencido)
                .map(this::toReservaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> buscarPorEstado(String estado) {
        EstadoReserva estadoEnum = EstadoReserva.valueOf(estado.toUpperCase());
        return reservaRepository.findByEstado(estadoEnum).stream()
                .map(this::actualizarEstadoSiVencido)
                .map(this::toReservaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> buscarPorEmpleadoId(Long empleadoId) {
        return reservaRepository.findByEmpleadoId(empleadoId).stream()
                .map(this::actualizarEstadoSiVencido)
                .map(this::toReservaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> buscarPorEmpleadoYFecha(Long empleadoId, String inicio, String fin) {
        LocalDateTime inicioDt = LocalDateTime.parse(inicio, FMT);
        LocalDateTime finDt = LocalDateTime.parse(fin, FMT);
        return reservaRepository.findByEmpleadoIdAndFechaBetween(empleadoId, inicioDt, finDt).stream()
                .map(this::actualizarEstadoSiVencido)
                .map(this::toReservaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorEmpleadoYFecha(Long empleadoId, String inicio, String fin) {
        LocalDateTime inicioDt = LocalDateTime.parse(inicio, FMT);
        LocalDateTime finDt = LocalDateTime.parse(fin, FMT);
        return reservaRepository.countByEmpleadoIdAndFechaBetween(empleadoId, inicioDt, finDt);
    }

    @Override
    @Transactional
    public ReservaResponse crearReserva(ReservaRequest request) {
        Huesped huesped = huespedRepository.findById(request.huespedId())
                .orElseThrow(() -> new ExcepcionNoEncontrada("Huésped no encontrado con id: " + request.huespedId()));

        Habitacion habitacion = habitacionRepository.findById(request.habitacionId())
                .orElseThrow(() -> new ExcepcionNoEncontrada("Habitación no encontrada con id: " + request.habitacionId()));

        Empleado empleado = empleadoRepository.findById(request.empleadoId())
                .orElseThrow(() -> new ExcepcionNoEncontrada("Empleado no encontrado con id: " + request.empleadoId()));

        if (request.numeroPersonas() > habitacion.getCapacidad()) {
            throw new ExcepcionEmpresarial(
                    "La habitación tiene capacidad para " + habitacion.getCapacidad() +
                    " personas, pero se registraron " + request.numeroPersonas());
        }

        LocalDateTime checkIn = LocalDateTime.parse(request.checkIn(), FMT);
        LocalDateTime checkOut = LocalDateTime.parse(request.checkOut(), FMT);

        if (!checkOut.isAfter(checkIn)) {
            throw new ExcepcionEmpresarial("El check-out debe ser posterior al check-in");
        }

        if (!reservaRepository.findOverlapping(request.habitacionId(), checkIn, checkOut).isEmpty()) {
            throw new ExcepcionEmpresarial("La habitación ya tiene una reserva en ese rango de fechas");
        }

        long noches = checkOut.toLocalDate().toEpochDay() - checkIn.toLocalDate().toEpochDay();
        noches = Math.max(noches, 1);
        double totalPagar = habitacion.getPrecio() * noches;

        Reserva reserva = new Reserva();
        reserva.setHuesped(huesped);
        reserva.setHabitacion(habitacion);
        reserva.setEmpleado(empleado);
        reserva.setCheckIn(checkIn);
        reserva.setCheckOut(checkOut);
        reserva.setNumeroPersonas(request.numeroPersonas());
        reserva.setTotalPagar(totalPagar);
        reserva.setEstado(EstadoReserva.PENDIENTE);

        reserva = reservaRepository.save(reserva);

        notificacionResolver.emitiNotificacion(
                "Nueva reserva: Hab " + habitacion.getNumero() + " - " + huesped.getNombre(),
                "RESERVAS");

        return toReservaResponse(reserva);
    }

    @Override
    @Transactional
    public ReservaResponse editarReserva(Long id, ReservaRequest request) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Reserva no encontrada con id: " + id));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ExcepcionEmpresarial("Solo se pueden editar reservas en estado PENDIENTE");
        }

        Huesped huesped = huespedRepository.findById(request.huespedId())
                .orElseThrow(() -> new ExcepcionNoEncontrada("Huésped no encontrado con id: " + request.huespedId()));

        Habitacion habitacion = habitacionRepository.findById(request.habitacionId())
                .orElseThrow(() -> new ExcepcionNoEncontrada("Habitación no encontrada con id: " + request.habitacionId()));

        Empleado empleado = empleadoRepository.findById(request.empleadoId())
                .orElseThrow(()-> new ExcepcionNoEncontrada("Empleado no encontrado con id : " + request.empleadoId()));

        if (request.numeroPersonas() > habitacion.getCapacidad()) {
            throw new ExcepcionEmpresarial(
                    "La habitación tiene capacidad para " + habitacion.getCapacidad() +
                    " personas, pero se registraron " + request.numeroPersonas());
        }

        LocalDateTime checkIn = LocalDateTime.parse(request.checkIn(), FMT);
        LocalDateTime checkOut = LocalDateTime.parse(request.checkOut(), FMT);

        if (!checkOut.isAfter(checkIn)) {
            throw new ExcepcionEmpresarial("El check-out debe ser posterior al check-in");
        }

        // Si cambió de habitación, liberar si estaba ocupada por check-in
        if (!reserva.getHabitacion().getId().equals(request.habitacionId())) {
            if (reserva.getEstado() == EstadoReserva.ACTIVA) {
                Habitacion habAnterior = reserva.getHabitacion();
                habAnterior.setEstado(EstadoHabitacion.DISPONIBLE);
                habitacionRepository.save(habAnterior);
            }
        }

        // Verificar solapamiento para la nueva configuración
        if (!reserva.getHabitacion().getId().equals(request.habitacionId()) ||
            !reserva.getCheckIn().equals(checkIn) ||
            !reserva.getCheckOut().equals(checkOut)) {
            List<Reserva> overlapping = reservaRepository.findOverlapping(
                    request.habitacionId(), checkIn, checkOut);
            overlapping = overlapping.stream()
                    .filter(r -> !r.getId().equals(id))
                    .toList();
            if (!overlapping.isEmpty()) {
                // Si cambió de habitación y hay solapamiento, revertir
                if (!reserva.getHabitacion().getId().equals(request.habitacionId())) {
                    if (reserva.getEstado() == EstadoReserva.ACTIVA) {
                        Habitacion habAnterior = reserva.getHabitacion();
                        habAnterior.setEstado(EstadoHabitacion.OCUPADA);
                        habitacionRepository.save(habAnterior);
                    }
                }
                throw new ExcepcionEmpresarial("La habitación ya tiene una reserva en ese rango de fechas");
            }
        }

        reserva.setHuesped(huesped);
        reserva.setHabitacion(habitacion);
        reserva.setEmpleado(empleado);
        reserva.setCheckIn(checkIn);
        reserva.setCheckOut(checkOut);
        reserva.setNumeroPersonas(request.numeroPersonas());

        reserva = reservaRepository.save(reserva);
        return toReservaResponse(reserva);
    }

    @Override
    @Transactional
    public ReservaResponse checkinReserva(Long id, MetodoPago metodoPago) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Reserva no encontrada con id: " + id));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ExcepcionEmpresarial("Solo se puede hacer check-in a reservas en estado PENDIENTE");
        }

        if (reserva.getTotalPagar() == null || reserva.getTotalPagar() <= 0) {
            throw new ExcepcionEmpresarial("La reserva no tiene un monto calculado");
        }

        Habitacion habitacion = reserva.getHabitacion();
        if (habitacion.getEstado() != EstadoHabitacion.DISPONIBLE) {
            throw new ExcepcionEmpresarial("La habitación no está disponible");
        }

        reserva.setMetodoPago(metodoPago);

        if (reserva.getCheckIn().isAfter(LocalDateTime.now())) {
            // Check-in futuro → solo prepago, no activar
            reserva.setFechaCheckIn(reserva.getCheckIn());
            reserva = reservaRepository.save(reserva);
        } else {
            // Check-in ya vencido → activar normal
            reserva.setEstado(EstadoReserva.ACTIVA);
            reserva.setFechaCheckIn(reserva.getCheckIn());

            habitacion.setEstado(EstadoHabitacion.OCUPADA);
            habitacionRepository.save(habitacion);

            reserva = reservaRepository.save(reserva);
        }

        // Registrar pago (prepago en ambos casos)
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(reserva.getTotalPagar());
        pago.setMetodoPago(metodoPago);
        pago.setFechaPago(LocalDateTime.now());
        pagoRepository.save(pago);

        // Registrar INGRESO en caja
        try {
            var cajaResponse = cajaService.obtenerOCrearCajaAbierta(reserva.getEmpleado().getId());
            cajaService.registrarIngreso(
                    cajaResponse.id(),
                    reserva.getId(),
                    reserva.getTotalPagar(),
                    "Check-in: Hab " + habitacion.getNumero() + " - " + reserva.getHuesped().getNombre()
            );
        } catch (Exception e) {
            System.err.println("Error al registrar ingreso en caja: " + e.getMessage());
        }

        notificacionResolver.emitiNotificacion(
                "Check-in: Hab " + habitacion.getNumero() + " - " + reserva.getHuesped().getNombre(),
                "RESERVAS");

        return toReservaResponse(reserva);
    }

    @Override
    @Transactional
    public ReservaResponse checkoutReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Reserva no encontrada con id: " + id));

        if (reserva.getEstado() != EstadoReserva.ACTIVA) {
            throw new ExcepcionEmpresarial("Solo se puede hacer checkout a reservas en estado ACTIVA");
        }

        reserva.setEstado(EstadoReserva.COMPLETADA);

        Habitacion habitacion = reserva.getHabitacion();
        habitacion.setEstado(EstadoHabitacion.EN_LIMPIEZA);
        habitacion.setLimpiezaInicio(LocalDateTime.now());
        habitacionRepository.save(habitacion);

        reserva = reservaRepository.save(reserva);

        notificacionResolver.emitiNotificacion(
                "Checkout: Hab " + habitacion.getNumero(), "RESERVAS");

        return toReservaResponse(reserva);
    }

    @Override
    @Transactional
    public ReservaResponse cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Reserva no encontrada con id: " + id));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ExcepcionEmpresarial("Solo se pueden cancelar reservas en estado PENDIENTE");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);

        Habitacion habitacion = reserva.getHabitacion();
        habitacion.setLimpiezaInicio(null);
        habitacionRepository.save(habitacion);

        reserva = reservaRepository.save(reserva);

        notificacionResolver.emitiNotificacion(
                "Reserva cancelada: Hab " + habitacion.getNumero(), "RESERVAS");

        return toReservaResponse(reserva);
    }

    @Override
    @Transactional
    public void eliminarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Reserva no encontrada con id: " + id));
        reservaRepository.delete(reserva);
    }

    private Reserva actualizarEstadoSiVencido(Reserva reserva) {
        if (reserva.getEstado() == EstadoReserva.ACTIVA
                && reserva.getCheckOut().isBefore(LocalDateTime.now())) {
            reserva.setEstado(EstadoReserva.COMPLETADA);

            Habitacion habitacion = reserva.getHabitacion();
            if (habitacion.getEstado() == EstadoHabitacion.OCUPADA) {
                habitacion.setEstado(EstadoHabitacion.EN_LIMPIEZA);
                habitacion.setLimpiezaInicio(LocalDateTime.now());
                habitacionRepository.save(habitacion);
            }

            reservaRepository.save(reserva);
        }
        return reserva;
    }

    private ReservaResponse toReservaResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getHuesped().getId(),
                reserva.getHuesped().getNombre() + " " + reserva.getHuesped().getApellido(),
                reserva.getHabitacion().getId(),
                reserva.getHabitacion().getNumero(),
                reserva.getEmpleado().getId(),
                reserva.getEmpleado().getNombre() + " " + reserva.getEmpleado().getApellido(),
                reserva.getCheckIn().format(FMT),
                reserva.getCheckOut().format(FMT),
                reserva.getNumeroPersonas(),
                reserva.getFechaCheckIn() != null ? reserva.getFechaCheckIn().toString() : null,
                reserva.getTotalPagar(),
                reserva.getMetodoPago(),
                reserva.getEstado()
        );
    }
}
