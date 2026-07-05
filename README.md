# Backend Hotel — Sistema de Gestión Hotelera

Backend del sistema de gestión hotelera "Hostal Señorío del Cinto". Proporciona una API GraphQL completa para administración de habitaciones, huéspedes, reservas, empleados, caja, facturación electrónica SUNAT y más.

---

## Stack Tecnológico

| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring GraphQL | 2.0.3 |
| Spring Security | 7.0.5 |
| Spring Data JPA | 4.0.5 |
| Hibernate | 7.2.12 |
| PostgreSQL | 16 |
| jjwt (JSON Web Token) | 0.12.6 |
| FreeMarker | (Spring Boot Starter) |
| Cloudinary | 1.39.0 |
| Caffeine Cache | 3.x |
| Project Lombok | Último |

---

## Arquitectura

```
┌─────────────┐    ┌──────────────────┐    ┌────────────────┐    ┌────────────┐
│   Cliente   │───>│  GraphQL Resolver│───>│  Service Layer │───>│ Repository │
│ (GraphiQL / │    │  (@Controller)   │    │  (@Service)    │    │  (JPA)     │
│  Frontend)  │    └──────────────────┘    └────────────────┘    └─────┬──────┘
└─────────────┘                                                       │
       │                                                      ┌───────▼──────┐
       │ REST (descarga XML, subida img)                     │  PostgreSQL  │
       └─────────────────────────────────────────────────────┘              │
                                                               └────────────┘
```

- **GraphQL** es el canal principal (mutations + queries + subscriptions)
- **REST** exclusivo para descarga de XML firmado y subida de imágenes
- **WebSockets** para notificaciones en tiempo real (vía GraphQL Subscriptions)
- **Scheduler** para procesos automáticos (check-in, check-out, limpieza)

---

## Estructura del Proyecto

```
Backend_Hotel/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .env
├── facturacion/
│   ├── certs/DEMO_Sunat.pfx            # Certificado digital SUNAT
│   └── storage/xmls/
│       ├── firmados/                    # XML firmados emitidos
│       ├── cdrs/                        # CDR recibidos de SUNAT
│       └── logs/                        # Logs de respuesta SOAP
│
└── src/main/java/com/hotel/backend_hotel/
    ├── BackendHotelApplication.java     # @SpringBootApplication + @EnableJpaAuditing + @EnableScheduling
    │
    ├── Auth/                            # Autenticación (login, register, logout, me)
    │   ├── entity/Usuario.java          # UserDetails impl, @ManyToMany -> roles
    │   ├── dto/                         # LoginRequest, LoginResponse, RegisterRequest
    │   ├── service/AuthService.java
    │   ├── service/Impl/AuthServiceImpl.java
    │   ├── resolver/AuthResolver.java   # GraphQL: login, register, logout, me
    │   └── repository/UserRepository.java
    │
    ├── Security/                        # Seguridad JWT
    │   ├── SecurityConfig.java          # CORS, CSRF, rutas públicas, session stateless
    │   ├── JwtAuthenticationFilter.java # Filtro JWT (Bearer token)
    │   ├── JwtUtils.java                # Generación y validación de tokens
    │   ├── UserDetailsServiceImpl.java  # Carga de usuarios
    │   └── ApplicationConfig.java       # AuthenticationManager, PasswordEncoder
    │
    ├── Tokens/                          # Blacklist de tokens revocados
    │   ├── TokenRevocado.java           # Entity (token_hash PK)
    │   ├── TokenRevocadoRepository.java
    │   └── TokenBlacklistService.java   # Caffeine cache + PostgreSQL
    │
    ├── RolPermisos/                     # Roles y permisos del sistema
    │   ├── entity/Rol.java
    │   ├── entity/Permiso.java
    │   ├── dto/                         # RolRequest, RolResponse, PermisoRequest, PermisoResponse
    │   ├── service/                     # RolService, PermisoService
    │   ├── resolver/RolPermisoResolver.java
    │   └── repository/                  # RolRepository, PermisoRepository
    │
    ├── Habitaciones/                    # Gestión de habitaciones
    │   ├── entity/Habitacion.java
    │   ├── dto/                         # HabitacionRequest, HabitacionResponse, HabitacionPage
    │   ├── service/HabitacionService.java
    │   ├── resolver/HabitacionResolver.java
    │   └── repository/HabitacionRepository.java
    │
    ├── Huesped/                         # Gestión de huéspedes
    │   ├── entity/Huesped.java
    │   ├── dto/                         # HuespedRequest, HuespedResponse, HuespedPage
    │   ├── service/HuespedService.java
    │   ├── resolver/HuespedResolver.java
    │   └── repository/HuespedRepository.java
    │
    ├── Reserva/                         # Gestión de reservas
    │   ├── entity/Reserva.java
    │   ├── dto/                         # ReservaRequest, ReservaResponse, ReservaPage
    │   ├── service/ReservaService.java
    │   ├── resolver/ReservaResolver.java
    │   └── repository/ReservaRepository.java
    │
    ├── Empleado/                        # Gestión de empleados
    │   ├── entity/Empleado.java         # Auto-crea Usuario al crear empleado
    │   ├── dto/                         # EmpleadoRequest, EmpleadoResponse
    │   ├── service/EmpleadoService.java
    │   ├── resolver/EmpleadoResolver.java
    │   └── repository/EmpleadoRepository.java
    │
    ├── Caja/                            # Caja chica y pagos
    │   ├── entity/Caja.java             # Caja única por turno
    │   ├── entity/MovimientoCaja.java   # Ingresos y egresos
    │   ├── entity/Pago.java             # Pagos vinculados a reservas
    │   ├── dto/                         # CajaRequest, CajaResponse, MovimientoRequest,
    │   │                                 # MovimientoResponse, ResumenCaja, PagoResponse
    │   ├── service/CajaService.java
    │   ├── resolver/CajaResolver.java
    │   └── repository/                  # CajaRepository, MovimientoRepository, PagoRepository
    │
    ├── Facturacion/                     # Facturación electrónica SUNAT
    │   ├── entity/Factura.java          # Comprobante emitido
    │   ├── entity/Emisor.java           # Empresa emisora
    │   ├── entity/DetalleFactura.java   # Líneas de detalle
    │   ├── entity/Serie.java            # Series (F001, B001) con @Version
    │   ├── dto/                         # FacturaRequest, FacturaResponse, EmisorRequest,
    │   │                                 # SerieRequest, DetalleResponse, ItemManualInput
    │   ├── service/FacturacionService.java
    │   ├── service/NumeracionService.java   # Correlativo secuencial
    │   ├── resolver/FacturacionResolver.java
    │   ├── FacturaController.java           # REST: descarga XML
    │   ├── sunat/
    │   │   ├── XmlUblGenerator.java         # Plantilla FTL → XML UBL 2.1
    │   │   ├── FirmaDigitalService.java     # Firma digital con certificado PFX
    │   │   ├── SunatSoapClient.java         # Envío SOAP a SUNAT
    │   │   ├── CalculosTributarios.java     # Cálculos IGV + totales
    │   │   └── CatalogosSunat.java          # Catálogos SUNAT
    │   └── repository/                  # FacturaRepository, EmisorRepository,
    │                                     # SerieRepository, DetalleFacturaRepository
    │
    ├── ServiciosAdicionales/            # Servicios extra (WiFi, alimentos, etc.)
    │   ├── entity/ServicioAdicional.java
    │   ├── dto/ServicioAdicionalRequest.java
    │   ├── service/ServicioAdicionalService.java
    │   ├── resolver/ServicioAdicionalResolver.java
    │   └── repository/ServicioAdicionalRepository.java
    │
    ├── ServiciosExternos/               # Integraciones externas
    │   ├── CloudinaryConfig.java
    │   ├── CloudinaryService.java       # Subida/eliminación de imágenes
    │   └── ImagenController.java        # REST: POST /api/upload/imagen
    │
    ├── Enums/                           # Enumeraciones del sistema
    │   ├── EstadoHabitacion.java        # DISPONIBLE, OCUPADA, EN_LIMPIEZA, MANTENIMIENTO, BLOQUEADA
    │   ├── EstadoReserva.java           # PENDIENTE, ACTIVA, COMPLETADA, CANCELADA
    │   ├── EstadoFactura.java           # BORRADOR, ACEPTADO, RECHAZADO, ANULADO
    │   ├── EstadoCaja.java              # ABIERTA, CERRADA
    │   ├── MetodoPago.java              # EFECTIVO, TARJETA, TRANSFERENCIA, YAPE, PLIN
    │   ├── TipoComprobante.java         # FACTURA("01"), BOLETA("03")
    │   ├── TipoDoc.java                 # DNI, PASAPORTE, CE, RUC
    │   ├── TipoMovimiento.java          # INGRESO, EGRESO
    │   ├── Cargo.java                   # RECEPCIONISTA, ADMINISTRADOR, AUDITOR, CAJERO
    │   ├── TurnoEmpleado.java           # DIA, NOCHE
    │   └── CategoriaServicio.java       # WIFI, ALIMENTACION, LAVANDERIA, GIMNASIO, SPA, OTROS
    │
    ├── common/                          # Componentes compartidos
    │   ├── entity/BaseEntity.java       # @MappedSuperclass: id, createdAt, updatedAt
    │   ├── Excepcion/
    │   │   ├── ExcepcionNoEncontrada.java
    │   │   ├── ExcepcionEmpresarial.java
    │   │   └── ControladorExcepcionesGlobales.java
    │   ├── RealTime/
    │   │   ├── NotificacionHotel.java
    │   │   └── NotificacionResolver.java    # GraphQL Subscription
    │   └── Config/
    │       ├── WebConfig.java               # CORS
    │       ├── GraphQLWebSocketConfig.java
    │       ├── WebSocketAuthInterceptor.java
    │       └── GraphQLExceptionHandler.java
    │
    └── Scheduler/
        └── HabitacionScheduler.java     # Auto-checkin (2min), auto-checkout (5min),
                                          # liberar habitaciones (1min)
```

---

## Configuración

### `application.properties`

| Propiedad | Descripción | Default |
|-----------|-------------|---------|
| `spring.datasource.url` | URL de conexión PostgreSQL | `jdbc:postgresql://localhost:${POSTGRES_PORT}/${POSTGRES_DB}` |
| `spring.datasource.username` | Usuario BD | `${POSTGRES_USER}` |
| `spring.datasource.password` | Contraseña BD | `${POSTGRES_PASSWORD}` |
| `spring.jpa.hibernate.ddl-auto` | Estrategia DDL | `update` |
| `spring.graphql.graphiql.enabled` | Habilitar GraphiQL | `true` |
| `spring.graphql.websocket.path` | Path WebSocket GraphQL | `/graphql` |
| `cloudinary.cloud-name` | Cloud name | `${CLOUDINARY_CLOUD_NAME}` |
| `cloudinary.api-key` | API Key | `${CLOUDINARY_API_KEY}` |
| `cloudinary.api-secret` | API Secret | `${CLOUDINARY_API_SECRET}` |
| `sunat.cert.path` | Ruta certificado PFX | `facturacion/certs/DEMO_Sunat.pfx` |
| `sunat.cert.password` | Contraseña PFX | `Jhonain321` |
| `sunat.storage.dir` | Directorio XML | `facturacion/storage/xmls` |
| `sunat.modo` | Modo SUNAT | `beta` (o `simulado`) |

### Variables de entorno (`.env`)

```
POSTGRES_DB=hotel_db
POSTGRES_USER=postgres_User
POSTGRES_PASSWORD=admin123
POSTGRES_PORT=5432
CLOUDINARY_CLOUD_NAME=dwit10r2m
CLOUDINARY_API_KEY=937947863335883
CLOUDINARY_API_SECRET=4nbuVgXZSH4Zm4hL9SAnAH759bc
CLOUDINARY_UPLOAD_PRESET=hotel_preset
```

---

## Inicio Rápido

### 1. Requisitos

- Java 21+
- Docker Desktop (para PostgreSQL)
- Maven Wrapper (incluido)

### 2. Iniciar Base de Datos

```bash
docker compose up -d
```

### 3. Configurar variables de entorno

Crear archivo `.env` en la raíz (ver sección anterior) o exportar las variables en el sistema.

### 4. Ejecutar

```bash
# Con Maven Wrapper:
./mvnw spring-boot:run

# O empaquetar y ejecutar:
./mvnw package -DskipTests
java -jar target/Backend_Hotel-0.0.1-SNAPSHOT.jar
```

### 5. Acceder a GraphiQL

```
http://localhost:8080/graphiql
```

### 6. Prueba rápida

```graphql
mutation Login {
  login(input: { username: "admin", password: "admin123" }) {
    token
    username
  }
}
```

---

## API GraphQL

Endpoint único: `POST /graphql` | WebSocket: `ws://localhost:8080/graphql`

### Auth

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Mutation | `login(input: LoginRequest!)` | Iniciar sesión |
| Mutation | `register(input: RegisterRequest!)` | Registrar usuario |
| Mutation | `logout(token: String!)` | Cerrar sesión (revoca token) |
| Query | `me` | Usuario autenticado actual |

### Habitaciones

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Query | `habitaciones` | Todas las habitaciones |
| Query | `habitacion(id: ID!)` | Por ID |
| Query | `habitacionPorNumero(numero: String!)` | Por número |
| Query | `habitacionesDisponibles` | Solo disponibles |
| Query | `habitacionesPorTipo(tipo: String!)` | Por tipo |
| Query | `habitacionesPaginadas(page: Int!, size: Int!)` | Paginado |
| Query | `filtrarHabitaciones(estado, tipo, piso, capacidad, precioMin, precioMax)` | Con filtros |
| Mutation | `crearHabitacion(input: HabitacionInput!)` | Crear |
| Mutation | `editarHabitacion(id: ID!, input: HabitacionInput!)` | Editar |
| Mutation | `cambiarEstadoHabitacion(id: ID!, estado: EstadoHabitacion!)` | Cambiar estado |
| Mutation | `eliminarHabitacion(id: ID!)` | Eliminar |

### Huéspedes

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Query | `huespedes` | Todos |
| Query | `huespedesPaginados(page: Int!, size: Int!)` | Paginado |
| Query | `huesped(id: ID!)` | Por ID |
| Query | `huespedPorDocumento(numeroDocumento: String!)` | Por documento |
| Mutation | `crearHuesped(input: HuespedInput!)` | Crear |
| Mutation | `editarHuesped(id: ID!, input: HuespedInput!)` | Editar |
| Mutation | `eliminarHuesped(id: ID!)` | Eliminar |

### Reservas

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Query | `reservas` | Todas |
| Query | `reservasPaginadas(page: Int!, size: Int!)` | Paginado |
| Query | `reserva(id: ID!)` | Por ID |
| Query | `reservasPorHuesped(huespedId: ID!)` | Por huésped |
| Query | `reservasPorHabitacion(habitacionId: ID!)` | Por habitación |
| Query | `reservasPorEstado(estado: EstadoReserva!)` | Por estado |
| Query | `reservasPorEmpleado(empleadoId: ID!)` | Por empleado |
| Query | `reservasPorEmpleadoYFecha(empleadoId: ID!, inicio: String!, fin: String!)` | Por empleado + fecha |
| Query | `conteoReservasPorEmpleadoYFecha(empleadoId: ID!, inicio: String!, fin: String!)` | Conteo |
| Mutation | `crearReserva(input: ReservaInput!)` | Crear |
| Mutation | `editarReserva(id: ID!, input: ReservaInput!)` | Editar |
| Mutation | `checkinReserva(id: ID!, metodoPago: MetodoPago!)` | Check-in |
| Mutation | `checkoutReserva(id: ID!)` | Check-out |
| Mutation | `cancelarReserva(id: ID!)` | Cancelar |
| Mutation | `eliminarReserva(id: ID!)` | Eliminar |

### Empleados

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Query | `empleados` | Todos |
| Query | `empleado(id: ID!)` | Por ID |
| Query | `empleadosPorCargo(cargo: Cargo!)` | Por cargo |
| Query | `empleadosPorTurno(turno: TurnoEmpleado!)` | Por turno |
| Query | `empleadosActivos` | Solo activos |
| Query | `empleadosConPagoPendiente` | Con pago pendiente |
| Mutation | `crearEmpleado(input: EmpleadoInput!)` | Crear (auto-crea Usuario) |
| Mutation | `editarEmpleado(id: ID!, input: EmpleadoInput!)` | Editar |
| Mutation | `eliminarEmpleado(id: ID!)` | Eliminar |
| Mutation | `cambiarEstadoEmpleado(id: ID!, activo: Boolean!)` | Activar/desactivar |
| Mutation | `asignarUsuarioAEmpleado(empleadoId: ID!, usuarioId: ID!)` | Asignar usuario |

### Caja

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Query | `cajas` | Todas |
| Query | `caja(id: ID!)` | Por ID |
| Query | `cajaAbierta` | Caja abierta actual |
| Query | `cajasPorFecha(inicio: String!, fin: String!)` | Por rango de fecha |
| Query | `cajasPorFechaPaginado(inicio: String!, fin: String!, page: Int!, size: Int!)` | Paginado |
| Query | `movimientosDeCaja(cajaId: ID!)` | Movimientos de una caja |
| Query | `movimientosDeCajaPaginados(cajaId: ID!, page: Int!, size: Int!)` | Paginado |
| Query | `resumenCaja(inicio: String!, fin: String!)` | Resumen financiero |
| Mutation | `abrirCaja(montoInicial: Float!)` | Abrir caja |
| Mutation | `cerrarCaja(id: ID!, montoFinal: Float!)` | Cerrar caja |
| Mutation | `registrarEgreso(input: EgresoInput!)` | Registrar egreso |

### Facturación

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Query | `emisorActivo` | Emisor configurado |
| Query | `emisor(id: ID!)` | Emisor por ID |
| Query | `series` | Series registradas |
| Query | `factura(id: ID!)` | Factura por ID |
| Query | `facturaPorNumero(serie: String!, correlativo: Int!)` | Por serie+correlativo |
| Query | `facturasPorReserva(reservaId: ID!)` | Por reserva |
| Query | `facturasPorHuesped(huespedId: ID!)` | Por huésped |
| Query | `facturasPaginadas(page: Int!, size: Int!, tipo, estado, fechaInicio, fechaFin)` | Paginado con filtros |
| Query | `detalleFactura(facturaId: ID!)` | Detalle (ítems) |
| Mutation | `crearEmisor(input: EmisorInput!)` | Configurar emisor |
| Mutation | `editarEmisor(id: ID!, input: EmisorInput!)` | Editar emisor |
| Mutation | `crearSerie(input: SerieInput!)` | Crear serie |
| Mutation | `generarFactura(input: FacturaInput!)` | Emitir comprobante |

### Roles y Permisos

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Query | `roles` | Todos los roles |
| Query | `rol(id: ID!)` | Rol por ID |
| Query | `permisos` | Todos los permisos |
| Query | `permiso(id: ID!)` | Permiso por ID |
| Mutation | `crearRol(nombre: String!, descripcion: String)` | Crear rol |
| Mutation | `editarRol(id: ID!, nombre: String!, descripcion: String)` | Editar rol |
| Mutation | `eliminarRol(id: ID!)` | Eliminar rol |
| Mutation | `asignarPermisosARol(rolId: ID!, permisosIds: [ID!]!)` | Asignar permisos |
| Mutation | `revocarPermisoDeRol(rolId: ID!, permisoId: ID!)` | Revocar permiso |
| Mutation | `crearPermiso(input: PermisoRequest!)` | Crear permiso |
| Mutation | `editarPermiso(id: ID!, input: PermisoRequest!)` | Editar permiso |
| Mutation | `eliminarPermiso(id: ID!)` | Eliminar permiso |

### Servicios Adicionales

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Query | `serviciosAdicionales` | Todos |
| Query | `servicioAdicional(id: ID!)` | Por ID |
| Mutation | `crearServicioAdicional(input: ServicioAdicionalInput!)` | Crear |
| Mutation | `editarServicioAdicional(id: ID!, input: ServicioAdicionalInput!)` | Editar |
| Mutation | `eliminarServicioAdicional(id: ID!)` | Eliminar |

### Suscripciones (WebSocket)

| Tipo | Operación | Descripción |
|------|-----------|-------------|
| Subscription | `notificacionesSistema` | Notificaciones en tiempo real |

---

## API REST

### Descargar XML Firmado

```
GET /api/facturacion/{id}/xml
```

Devuelve el archivo XML firmado de una factura como `application/xml`.

### Subir Imagen

```
POST /api/upload/imagen
Content-Type: multipart/form-data

file: (archivo de imagen)
```

Devuelve `{ "url": "https://res.cloudinary.com/..." }`.

---

## Seguridad y Autenticación

### Flujo de autenticación

1. El cliente envía `mutation login { ... }` con username y password
2. El servidor valida credenciales y retorna un JWT (expira en 12 horas)
3. El cliente envía el JWT en cada request como `Authorization: Bearer <token>`
4. `JwtAuthenticationFilter` extrae y valida el token, establece el contexto de seguridad
5. Los resolvers GraphQL usan `@PreAuthorize` con permisos específicos

### Permisos del sistema

Los permisos siguen el patrón `{modulo}:{accion}`:

| Módulo | Acciones |
|--------|----------|
| `habitaciones` | `ver`, `crear`, `editar`, `eliminar` |
| `huespedes` | `ver`, `crear`, `editar`, `eliminar` |
| `reservas` | `ver`, `crear`, `editar`, `cancelar`, `checkin`, `checkout` |
| `empleados` | `ver`, `crear`, `editar`, `eliminar` |
| `caja` | `ver`, `abrir`, `cerrar`, `registrar` |
| `facturacion` | `ver`, `emitir`, `configurar` |
| `roles` | `ver`, `crear`, `editar`, `eliminar` |
| `servicios` | `ver`, `crear`, `editar`, `eliminar` |

### Blacklist de tokens

- Los tokens revocados se almacenan en Caffeine Cache (TTL 30 min) y PostgreSQL
- Al hacer `logout`, el token se agrega a la blacklist
- Un scheduler limpia tokens expirados cada 6 horas

### Manejo de errores

El `ControladorExcepcionesGlobales` captura todas las excepciones y las retorna como errores GraphQL tipados (siempre con HTTP 200).

---

## Base de Datos

### Tablas (18)

| Tabla | Entidad | Descripción |
|-------|---------|-------------|
| `usuarios` | Usuario | Usuarios del sistema |
| `roles` | Rol | Roles (SUPERADMIN, ADMINISTRADOR, etc.) |
| `permisos` | Permiso | Permisos individuales |
| `usuario_roles` | - | Join usuarios ↔ roles |
| `rol_permisos` | - | Join roles ↔ permisos |
| `habitaciones` | Habitacion | Habitaciones del hotel |
| `huespedes` | Huesped | Huéspedes/clientes |
| `empleados` | Empleado | Empleados del hotel |
| `reservas` | Reserva | Reservas (check-in/out) |
| `facturas` | Factura | Comprobantes electrónicos |
| `detalles_factura` | DetalleFactura | Líneas de detalle de factura |
| `emisores` | Emisor | Datos de la empresa emisora |
| `series` | Serie | Series de facturación (F001, B001) |
| `cajas` | Caja | Apertura/cierre de caja |
| `movimientos_caja` | MovimientoCaja | Ingresos y egresos de caja |
| `pagos` | Pago | Pagos registrados |
| `servicios_adicionales` | ServicioAdicional | Servicios extra del hotel |
| `tokens_revocados` | TokenRevocado | Tokens JWT revocados |

### Esquema relacional

```
usuarios ──── usuario_roles ──── roles ──── rol_permisos ──── permisos
  │
empleados (usuario_id FK)
  │
reservas (huesped_id, habitacion_id, empleado_id FK)
  │
facturas (emisor_id, serie_id, huesped_id, reserva_id FK)
  │
detalles_factura (factura_id FK)
  │
cajas (empleado_id FK)
  │
movimientos_caja (caja_id, reserva_id FK)
  │
pagos (reserva_id FK)
```

---

## Facturación Electrónica SUNAT

### Flujo de emisión

```
FacturaRequest
      │
      ▼
  1. Validar emisor activo y serie disponible
      │
      ▼
  2. Obtener correlativo (NumeracionService — optimistic lock)
      │
      ▼
  3. Crear Factura + DetallesFactura (desde reservas + items manuales)
      │
      ▼
  4. Calcular totales (CalculosTributarios)
      │
      ├── 5a. Generar XML UBL 2.1 (XmlUblGenerator — FreeMarker)
      │
      ├── 5b. Firmar XML con certificado PFX (FirmaDigitalService)
      │
      ├── 5c. Comprimir XML firmado en ZIP
      │
      ├── 5d. Enviar vía SOAP a SUNAT (SunatSoapClient)
      │
      └── 5e. Procesar CDR de respuesta
      │         - Código 0  → ACEPTADO
      │         - Otro código → RECHAZADO (con mensaje de error)
      │
      ▼
  6. Guardar factura actualizada en BD
      │
      ▼
  7. Notificar en tiempo real vía WebSocket
      │
      ▼
  FacturaResponse
```

### Modos de operación

| Modo | Configuración | Comportamiento |
|------|---------------|----------------|
| **Beta** (real) | `sunat.modo=beta` | Envía SOAP a SUNAT beta (`e-beta.sunat.gob.pe`) |
| **Simulado** | `sunat.modo=simulado` | Genera CDR simulado localmente, no llama a SUNAT |

### Componentes

| Clase | Responsabilidad |
|-------|----------------|
| **`XmlUblGenerator`** | Genera XML UBL 2.1 usando plantilla FreeMarker (`ubl-invoice.ftl`). Incluye datos del emisor, cliente, totales, líneas de detalle, y firma digital. |
| **`FirmaDigitalService`** | Carga certificado PFX, extrae llave privada y firma el XML usando JSR 105 (XML Digital Signature) con RSA-SHA256. |
| **`SunatSoapClient`** | Construye envelope SOAP, comprime XML firmado en ZIP, envía a SUNAT vía `HttpClient`, parsea respuesta SOAP y extrae CDR. |
| **`CalculosTributarios`** | IGV al 18%, factor 1.18. Calcula valorUnitario, precioUnitario, IGV e importe por línea. Suma totales. |
| **`CatalogosSunat`** | Mapea tipos de documento SUNAT (DNI→1, CE→4, RUC→6). Valida que factura requiera RUC. |

### Certificado digital

- Archivo: `facturacion/certs/DEMO_Sunat.pfx`
- Contraseña: `Jhonain321`
- Formato: PKCS#12

### Almacenamiento

```
facturacion/storage/xmls/
├── firmados/      # XML firmados listos para enviar
├── cdrs/          # CDR recibidos (ZIP original + XML extraído)
└── logs/          # Logs de respuesta SOAP sin procesar
```

---

## Notificaciones en Tiempo Real

Las notificaciones se envían a través de GraphQL Subscriptions.

### Suscripción

```graphql
subscription {
  notificacionesSistema {
    id
    mensaje
    modulo
  }
}
```

### Módulos que disparan notificaciones

- **RESERVAS**: check-in, check-out, cancelación
- **HABITACIONES**: cambio de estado
- **CAJA**: apertura, cierre, movimiento registrado

### Implementación

Usa `Sinks.Many<NotificacionHotel>` de Reactor para emitir eventos. Todos los clientes conectados reciben el mensaje.

---

## Lógica de Negocio Clave

### Caja única por turno

- Solo una caja puede estar abierta a la vez en todo el hotel
- El turno (DÍA/NOCHE) se detecta automáticamente según la hora actual
- Cualquier empleado puede abrir la caja o registrar movimientos
- Al hacer check-in de una reserva, se registra automáticamente un INGRESO en la caja abierta

### Creación automática de usuario al registrar empleado

- `username` = `{nombre}.{apellido}` (minúsculas)
- `password` = mismo username
- `rol` = según cargo del empleado
- Se notifica al empleado para que cambie su contraseña

### Facturación multi-reserva + items manuales

- Una factura puede incluir múltiples reservas del mismo cliente
- Se pueden agregar items manuales adicionales (ej. consumo de restaurant)
- El backend genera una línea de detalle por cada noche de cada reserva
- Los items manuales se persisten como `DetalleFactura` igual que los de reserva
- Para emitir FACTURA se requiere RUC (11 dígitos) + Razón Social

### Scheduler automático

- **Cada 1 minuto**: verifica reservas con check-in vencido y las pasa a estado ACTIVA (auto-checkin)
- **Cada 5 minutos**: verifica reservas ACTIVAS con check-out vencido y las completa (auto-checkout)
- **Cada 1 minuto**: libera habitaciones que estaban en limpieza después de X tiempo

### Reserva y cálculo de pagos

- `totalPagar` = precio de la habitación × número de noches
- Al hacer check-in, se registra un Pago y un movimiento INGRESO en caja
- Estados: PENDIENTE → ACTIVA → COMPLETADA | CANCELADA

---

## Manejo de Errores

| Excepción | Uso |
|-----------|-----|
| `ExcepcionNoEncontrada` | Recurso no encontrado (404 semántico) |
| `ExcepcionEmpresarial` | Error de negocio (ej. "Habitación no disponible", "Caja ya abierta") |
| `AccessDeniedException` | Sin permisos suficientes (retorna error FORBIDDEN GraphQL) |

Todas las excepciones se traducen a errores GraphQL tipados con el formato estándar (`message`, `locations`, `path`, `extensions`).

---

## Extensión: Reportes/Dashboard

Los siguientes métodos de repositorio están disponibles para consultas agregadas:

| Repositorio | Método | Descripción |
|-------------|--------|-------------|
| `HabitacionRepository` | `countByEstado(EstadoHabitacion)` | Contar habitaciones por estado |
| `ReservaRepository` | `findByCheckInBetween(LocalDateTime, LocalDateTime)` | Reservas con check-in en rango |
| `ReservaRepository` | `findByCheckOutBetween(LocalDateTime, LocalDateTime)` | Reservas con check-out en rango |
| `FacturaRepository` | `sumTotalByFechaEmisionBetween(LocalDate, LocalDate)` | Suma total de facturas en rango |
| `MovimientoRepository` | `sumByTipoAndFechaBetween(TipoMovimiento, LocalDateTime, LocalDateTime)` | Suma de ingresos/egresos por tipo+periodo |

---

## Licencia

Proyecto académico — Grupo 04, Desarrollo de Aplicaciones Distribuidas (9° ciclo).
