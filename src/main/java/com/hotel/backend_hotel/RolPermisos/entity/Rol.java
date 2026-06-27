package com.hotel.backend_hotel.RolPermisos.entity;

import com.hotel.backend_hotel.common.entity.BaseEntity;
import com.hotel.backend_hotel.Auth.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder //patron builder
@Data //generador de get - set - tostring - equals
@AllArgsConstructor // contructor vacio
@NoArgsConstructor // constructor sobrecargado
@Entity // para que reconozca hibernate que es un tabla
@Table(name = "roles") // nombre de la tabla en la base datos
public class Rol extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "rol_permisos",
            joinColumns = @JoinColumn(name = "rol_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Permiso> permisos = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Usuario> usuarios = new HashSet<>();
}