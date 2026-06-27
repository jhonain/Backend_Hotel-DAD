package com.hotel.backend_hotel.RolPermisos.resolver;

import com.hotel.backend_hotel.RolPermisos.dto.PermisoRequest;
import com.hotel.backend_hotel.RolPermisos.dto.PermisoResponse;
import com.hotel.backend_hotel.RolPermisos.dto.RolRequest;
import com.hotel.backend_hotel.RolPermisos.dto.RolResponse;
import com.hotel.backend_hotel.RolPermisos.service.PermisoService;
import com.hotel.backend_hotel.RolPermisos.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RolPermisoResolver {

    private final RolService rolService;
    private final PermisoService permisoService;

    // ========== QUERIES - ROLES ==========

    @QueryMapping
    @PreAuthorize("hasAuthority('roles:ver')")
    public List<RolResponse> roles() {
        return rolService.ListarRoles();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('roles:ver')")
    public RolResponse rol(@Argument Long id) {
        return rolService.BuscarPorID(id);
    }

    // ========== QUERIES - PERMISOS ==========

    @QueryMapping
    @PreAuthorize("hasAuthority('permisos:ver')")
    public List<PermisoResponse> permisos() {
        return permisoService.listarPermisos();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('permisos:ver')")
    public PermisoResponse permiso(@Argument Long id) {
        return permisoService.BuscarPorId(id);
    }

    // ========== MUTATIONS - ROLES ==========

    @MutationMapping
    @PreAuthorize("hasAuthority('roles:crear')")
    public RolResponse crearRol(@Argument String nombre, @Argument String descripcion) {
        RolRequest request = new RolRequest(nombre, descripcion);
        return rolService.CrearRol(request);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('roles:editar')")
    public RolResponse editarRol(@Argument Long id, @Argument String nombre, @Argument String descripcion) {
        return rolService.ActualizarRol(id, nombre, descripcion);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('roles:eliminar')")
    public Boolean eliminarRol(@Argument Long id) {
        rolService.EliminarRol(id);
        return true;
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('roles:editar')")
    public RolResponse asignarPermisosARol(@Argument Long rolId, @Argument List<Long> permisosIds) {
        return rolService.asignarPermisos(rolId, permisosIds);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('roles:editar')")
    public RolResponse revocarPermisoDeRol(@Argument Long rolId, @Argument Long permisoId) {
        return rolService.RevocarPermiso(rolId, permisoId);
    }

    // ========== MUTATIONS - PERMISOS ← NUEVO ==========

    @MutationMapping
    @PreAuthorize("hasAuthority('permisos:crear')")
    public PermisoResponse crearPermiso(
            @Argument String codigo,
            @Argument String descripcion,
            @Argument String modulo
    ) {
        PermisoRequest request = new PermisoRequest(codigo, descripcion, modulo);
        return permisoService.CrearPermiso(request);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('permisos:editar')")
    public PermisoResponse editarPermiso(
            @Argument Long id,
            @Argument String codigo,
            @Argument String descripcion,
            @Argument String modulo
    ) {
        PermisoRequest request = new PermisoRequest(codigo, descripcion, modulo);
        return permisoService.ActualizarPermiso(id, request);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('permisos:eliminar')")
    public Boolean eliminarPermiso(@Argument Long id) {
        permisoService.eliminarPermiso(id);
        return true;
    }
}