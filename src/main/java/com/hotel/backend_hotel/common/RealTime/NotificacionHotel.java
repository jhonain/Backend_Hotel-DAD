package com.hotel.backend_hotel.common.RealTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionHotel {
    private Long id;
    private String mensaje;
    private String modulo;
}
