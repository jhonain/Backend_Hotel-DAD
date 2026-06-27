package com.hotel.backend_hotel.Huesped.entity;

import com.hotel.backend_hotel.Enums.TipoDoc;
import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "huespedes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Huesped extends BaseEntity {
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(name = "tipo_documento", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDoc tipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(nullable = true, unique = true,  length = 150)
    private String email;



    @Column(nullable = true)
    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String telefono;

    @Column(nullable = false)
    private String nacionalidad;

}
