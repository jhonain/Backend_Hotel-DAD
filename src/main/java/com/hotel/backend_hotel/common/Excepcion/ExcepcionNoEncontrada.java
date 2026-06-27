package com.hotel.backend_hotel.common.Excepcion;

public class ExcepcionNoEncontrada extends RuntimeException {

    public ExcepcionNoEncontrada(String resource, Long id){
        super(resource + "no encontrado con id:"+id);
    }
    public  ExcepcionNoEncontrada(String message){
        super(message);
    }
}
