package com.hotel.backend_hotel.common.RealTime;

import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import jakarta.annotation.PostConstruct;

@Controller
public class NotificacionResolver {

    // Un Sink funciona como un canal/bus de datos donde puedes empujar mensajes desde cualquier servicio
    private Sinks.Many<NotificacionHotel> sink;

    @PostConstruct
    public void init() {
        // Configuramos el canal para que permita múltiples suscriptores en tiempo real
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    // 🌐 Este método mantiene abierto el WebSocket con React
    @SubscriptionMapping
    public Flux<NotificacionHotel> notificacionesSistema() {
        return sink.asFlux();
    }

    // Método auxiliar público para emitir eventos desde tus otros servicios (ej: Reservas, Caja)
    public void emitiNotificacion(String mensaje, String modulo) {
        NotificacionHotel notificacion = new NotificacionHotel(
                System.currentTimeMillis(), // ID temporal
                mensaje,
                modulo
        );
        sink.tryEmitNext(notificacion);
    }
}
