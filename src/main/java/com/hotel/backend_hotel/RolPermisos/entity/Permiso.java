// roles/entity/Permiso.java
package com.hotel.backend_hotel.RolPermisos.entity;

import com.hotel.backend_hotel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permisos")
public class Permiso extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String codigo; // ej: "habitaciones:crear", "reservas:ver"

    private String descripcion;

    private String modulo;

    @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Rol> roles = new HashSet<>();
}