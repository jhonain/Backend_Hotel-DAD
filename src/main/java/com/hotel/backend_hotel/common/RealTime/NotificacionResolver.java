package com.hotel.backend_hotel.common.RealTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import jakarta.annotation.PostConstruct;

@Controller
public class NotificacionResolver {

    private static final Logger log = LoggerFactory.getLogger(NotificacionResolver.class);

    private Sinks.Many<NotificacionHotel> sink;

    @PostConstruct
    public void init() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer(256, false);
    }

    @SubscriptionMapping
    public Flux<NotificacionHotel> notificacionesSistema() {
        return sink.asFlux();
    }

    public void emitiNotificacion(String mensaje, String modulo) {
        NotificacionHotel notificacion = new NotificacionHotel(
                System.currentTimeMillis(),
                mensaje,
                modulo
        );
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            emitir(notificacion);
                        }
                    });
        } else {
            emitir(notificacion);
        }
    }

    private void emitir(NotificacionHotel notificacion) {
        Sinks.EmitResult resultado = sink.tryEmitNext(notificacion);
        if (resultado != Sinks.EmitResult.OK) {
            log.warn("[NOTIFY] Error al emitir '{}': {}", notificacion.getMensaje(), resultado);
        }
    }
}