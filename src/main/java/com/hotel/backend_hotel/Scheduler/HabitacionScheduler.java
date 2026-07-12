package com.hotel.backend_hotel.Scheduler;

import com.hotel.backend_hotel.Enums.EstadoHabitacion;
import com.hotel.backend_hotel.Enums.EstadoReserva;
import com.hotel.backend_hotel.Habitaciones.entity.Habitacion;
import com.hotel.backend_hotel.Habitaciones.repository.HabitacionRepository;
import com.hotel.backend_hotel.Reserva.entity.Reserva;
import com.hotel.backend_hotel.Reserva.repository.ReservaRepository;
import com.hotel.backend_hotel.common.RealTime.NotificacionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HabitacionScheduler {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;
    private final NotificacionResolver notificacionResolver;

    @Scheduled(fixedRate = 15000)
    @Transactional
    public void autoCheckin() {
        List<Reserva> pendientes = reservaRepository.findByEstadoAndCheckInBefore(
                EstadoReserva.PENDIENTE, LocalDateTime.now());

        for (Reserva r : pendientes) {
            r.setEstado(EstadoReserva.ACTIVA);
            r.setFechaCheckIn(r.getCheckIn());

            Habitacion h = r.getHabitacion();
            h.setEstado(EstadoHabitacion.OCUPADA);

            reservaRepository.save(r);
            habitacionRepository.save(h);

            notificacionResolver.emitiNotificacion(
                    "Check-in automático: Hab " + h.getNumero() + " - " + r.getHuesped().getNombre(), "RESERVAS");
        }
    }

    @Scheduled(fixedRate = 15000)
    @Transactional
    public void autoCheckoutContinuo() {
        List<Reserva> vencidas = reservaRepository.findByEstadoAndCheckOutBefore(
                EstadoReserva.ACTIVA, LocalDateTime.now());

        for (Reserva r : vencidas) {
            r.setEstado(EstadoReserva.COMPLETADA);

            Habitacion h = r.getHabitacion();
            h.setEstado(EstadoHabitacion.EN_LIMPIEZA);
            h.setLimpiezaInicio(LocalDateTime.now());

            reservaRepository.save(r);
            habitacionRepository.save(h);

            notificacionResolver.emitiNotificacion(
                    "Check-out automático: Hab " + h.getNumero(), "RESERVAS");
        }
    }

    @Scheduled(fixedRate = 15000)
    @Transactional
    public void liberarHabitaciones() {
        if (LocalDateTime.now().getHour() >= 18) return;

        List<Habitacion> enLimpieza = habitacionRepository.findByEstado(EstadoHabitacion.EN_LIMPIEZA);
        LocalDateTime limite = LocalDateTime.now().minusMinutes(60);

        for (Habitacion h : enLimpieza) {
            if (h.getLimpiezaInicio() != null && h.getLimpiezaInicio().isBefore(limite)) {
                h.setEstado(EstadoHabitacion.DISPONIBLE);
                h.setLimpiezaInicio(null);

                habitacionRepository.save(h);

                notificacionResolver.emitiNotificacion(
                        "Hab " + h.getNumero() + " disponible", "HABITACIONES");
            }
        }
    }
}