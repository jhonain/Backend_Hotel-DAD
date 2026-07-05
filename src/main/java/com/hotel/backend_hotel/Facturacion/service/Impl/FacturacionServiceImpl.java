package com.hotel.backend_hotel.Facturacion.service.Impl;

import com.hotel.backend_hotel.Enums.EstadoFactura;
import com.hotel.backend_hotel.Enums.MetodoPago;
import com.hotel.backend_hotel.Enums.TipoComprobante;
import com.hotel.backend_hotel.Facturacion.dto.*;
import com.hotel.backend_hotel.Facturacion.entity.DetalleFactura;
import com.hotel.backend_hotel.Facturacion.entity.Emisor;
import com.hotel.backend_hotel.Facturacion.entity.Factura;
import com.hotel.backend_hotel.Facturacion.entity.Serie;
import com.hotel.backend_hotel.Facturacion.repository.EmisorRepository;
import com.hotel.backend_hotel.Facturacion.repository.FacturaRepository;
import com.hotel.backend_hotel.Facturacion.repository.SerieRepository;
import com.hotel.backend_hotel.Facturacion.repository.DetalleFacturaRepository;
import com.hotel.backend_hotel.Facturacion.service.FacturacionService;
import com.hotel.backend_hotel.Facturacion.service.NumeracionService;
import com.hotel.backend_hotel.Facturacion.sunat.CalculosTributarios;
import com.hotel.backend_hotel.Facturacion.sunat.CatalogosSunat;
import com.hotel.backend_hotel.Facturacion.sunat.FirmaDigitalService;
import com.hotel.backend_hotel.Facturacion.sunat.SunatSoapClient;
import com.hotel.backend_hotel.Facturacion.sunat.XmlUblGenerator;
import com.hotel.backend_hotel.Huesped.entity.Huesped;
import com.hotel.backend_hotel.Huesped.repository.HuespedRepository;
import com.hotel.backend_hotel.Reserva.entity.Reserva;
import com.hotel.backend_hotel.Reserva.repository.ReservaRepository;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionEmpresarial;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import com.hotel.backend_hotel.common.RealTime.NotificacionResolver;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturacionServiceImpl implements FacturacionService {

    private static final Logger log = LoggerFactory.getLogger(FacturacionServiceImpl.class);

    private final EmisorRepository emisorRepository;
    private final SerieRepository serieRepository;
    private final FacturaRepository facturaRepository;
    private final HuespedRepository huespedRepository;
    private final ReservaRepository reservaRepository;
    private final NumeracionService numeracionService;
    private final XmlUblGenerator xmlUblGenerator;
    private final FirmaDigitalService firmaDigitalService;
    private final SunatSoapClient sunatSoapClient;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final NotificacionResolver notificacionResolver;

    @Override
    @Transactional
    public EmisorResponse crearEmisor(EmisorRequest request) {
        if (emisorRepository.findByRuc(request.ruc()).isPresent()) {
            throw new ExcepcionEmpresarial("Ya existe un emisor con RUC: " + request.ruc());
        }
        Emisor emisor = new Emisor();
        emisor.setRuc(request.ruc());
        emisor.setRazonSocial(request.razonSocial());
        emisor.setNombreComercial(request.nombreComercial());
        emisor.setDireccion(request.direccion());
        emisor.setUbigeo(request.ubigeo());
        emisor.setDepartamento(request.departamento());
        emisor.setProvincia(request.provincia());
        emisor.setDistrito(request.distrito());
        emisor.setUsuarioSol(request.usuarioSol());
        emisor.setClaveSol(request.claveSol());
        emisor.setPorcentajeIgv(request.porcentajeIgv() != null ? request.porcentajeIgv() : new java.math.BigDecimal("10.50"));
        emisor = emisorRepository.save(emisor);
        return toEmisorResponse(emisor);
    }

    @Override
    @Transactional
    public EmisorResponse editarEmisor(Long id, EmisorRequest request) {
        Emisor emisor = emisorRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Emisor no encontrado: " + id));
        if (!emisor.getRuc().equals(request.ruc()) &&
                emisorRepository.findByRuc(request.ruc()).isPresent()) {
            throw new ExcepcionEmpresarial("Ya existe un emisor con RUC: " + request.ruc());
        }
        emisor.setRuc(request.ruc());
        emisor.setRazonSocial(request.razonSocial());
        emisor.setNombreComercial(request.nombreComercial());
        emisor.setDireccion(request.direccion());
        emisor.setUbigeo(request.ubigeo());
        emisor.setDepartamento(request.departamento());
        emisor.setProvincia(request.provincia());
        emisor.setDistrito(request.distrito());
        emisor.setUsuarioSol(request.usuarioSol());
        emisor.setClaveSol(request.claveSol());
        if (request.porcentajeIgv() != null) {
            emisor.setPorcentajeIgv(request.porcentajeIgv());
        }
        emisorRepository.save(emisor);
        return toEmisorResponse(emisor);
    }

    @Override
    @Transactional(readOnly = true)
    public EmisorResponse buscarEmisorPorId(Long id) {
        return toEmisorResponse(emisorRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Emisor no encontrado: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public EmisorResponse buscarEmisorActivo() {
        return emisorRepository.findByActivoTrue()
                .map(this::toEmisorResponse)
                .orElseThrow(() -> new ExcepcionEmpresarial("No hay un emisor activo configurado"));
    }

    @Override
    @Transactional
    public SerieResponse crearSerie(SerieRequest request) {
        if (serieRepository.findByTipoComprobanteAndActivoTrue(request.tipoComprobante()).isPresent()) {
            throw new ExcepcionEmpresarial("Ya existe una serie activa para " + request.tipoComprobante());
        }
        Serie serie = new Serie();
        serie.setSerie(request.serie());
        serie.setCorrelativo(0);
        serie.setTipoComprobante(request.tipoComprobante());
        serie = serieRepository.save(serie);
        return toSerieResponse(serie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SerieResponse> listarSeries() {
        return serieRepository.findAll().stream().map(this::toSerieResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SerieResponse buscarSeriePorTipo(TipoComprobante tipo) {
        return serieRepository.findByTipoComprobanteAndActivoTrue(tipo)
                .map(this::toSerieResponse)
                .orElseThrow(() -> new ExcepcionEmpresarial("No hay serie activa para " + tipo));
    }

    @Override
    @Transactional
    public FacturaResponse generarFactura(FacturaRequest request) {
        Emisor emisor = emisorRepository.findByActivoTrue()
                .orElseThrow(() -> new ExcepcionEmpresarial("No hay un emisor activo configurado"));

        Serie serie = serieRepository.findByTipoComprobanteAndActivoTrue(request.tipoComprobante())
                .orElseThrow(() -> new ExcepcionEmpresarial("No hay serie activa para " + request.tipoComprobante()));

        if (request.tipoComprobante() == TipoComprobante.FACTURA &&
                (request.clienteNumeroDoc() == null || request.clienteRazonSocial() == null)) {
            throw new ExcepcionEmpresarial("Factura requiere RUC y Razón Social del cliente");
        }

        Huesped huesped = null;
        if (request.clienteId() != null) {
            huesped = huespedRepository.findById(request.clienteId())
                    .orElseThrow(() -> new ExcepcionNoEncontrada("Huésped no encontrado: " + request.clienteId()));
        }

        List<Reserva> reservas = new ArrayList<>();
        if (request.reservaIds() != null) {
            for (Long rid : request.reservaIds()) {
                Reserva r = reservaRepository.findById(rid)
                        .orElseThrow(() -> new ExcepcionNoEncontrada("Reserva no encontrada: " + rid));
                reservas.add(r);
                if (huesped == null) huesped = r.getHuesped();
            }
        }

        if (huesped == null && request.clienteId() == null) {
            throw new ExcepcionEmpresarial("Debe especificar un cliente o al menos una reserva");
        }

        int correlativo = numeracionService.siguienteCorrelativo(serie.getId());

        Factura factura = new Factura();
        factura.setEmisor(emisor);
        factura.setSerie(serie);
        factura.setTipoComprobante(request.tipoComprobante());
        factura.setSerieCodigo(serie.getSerie());
        factura.setCorrelativo(correlativo);
        factura.setFechaEmision(LocalDate.now());
        factura.setFechaVencimiento(LocalDate.now().plusDays(7));
        factura.setHuesped(huesped);
        factura.setMetodoPago(huesped != null ? MetodoPago.EFECTIVO : MetodoPago.EFECTIVO);

        if (request.tipoComprobante() == TipoComprobante.FACTURA) {
            String tipoDoc = request.clienteTipoDoc();
            if (tipoDoc == null || tipoDoc.isBlank()) {
                tipoDoc = CatalogosSunat.detectarTipoDoc(request.clienteNumeroDoc());
            }
            factura.setClienteTipoDoc(tipoDoc);
            factura.setClienteNumeroDoc(request.clienteNumeroDoc());
            factura.setClienteRazonSocial(request.clienteRazonSocial());
            factura.setClienteDireccion(request.clienteDireccion());
        } else if (huesped != null) {
            factura.setClienteTipoDoc(CatalogosSunat.tipoDocToSunat(huesped.getTipoDocumento()));
            factura.setClienteNumeroDoc(huesped.getNumeroDocumento());
            factura.setClienteRazonSocial(huesped.getNombre() + " " + huesped.getApellido());
            factura.setClienteDireccion(null);
        }

        factura = facturaRepository.save(factura);

        List<DetalleFactura> detalles = new ArrayList<>();
        int itemNum = 1;

        for (Reserva r : reservas) {
            long noches = ChronoUnit.DAYS.between(r.getCheckIn().toLocalDate(), r.getCheckOut().toLocalDate());
            noches = Math.max(noches, 1);
            double precioPorNoche = r.getTotalPagar() / noches;

            for (int i = 1; i <= noches; i++) {
                CalculosTributarios.LineaCalculada linea = CalculosTributarios.calcularLineaDetalle(1, precioPorNoche / CalculosTributarios.IGV_FACTOR.doubleValue());
                DetalleFactura detalle = new DetalleFactura();
                detalle.setFactura(factura);
                detalle.setItem(itemNum++);
                detalle.setDescripcion("Hospedaje " + r.getHabitacion().getNumero() + " - Noche " + i);
                detalle.setCantidad(1.0);
                detalle.setUnidadMedida("ZZ");
                detalle.setValorUnitario(linea.valorUnitario().doubleValue());
                detalle.setPrecioUnitario(linea.precioUnitario().doubleValue());
                detalle.setIgv(linea.igv().doubleValue());
                detalle.setPorcentajeIgv(10.5);
                detalle.setValorTotal(linea.valorTotal().doubleValue());
                detalle.setImporteTotal(linea.importeTotal().doubleValue());
                detalles.add(detalle);
            }
        }

        if (request.itemsExtra() != null) {
            for (ItemManualInput extra : request.itemsExtra()) {
                double valorUnitarioSinIgv = extra.precioUnitario() / CalculosTributarios.IGV_FACTOR.doubleValue();
                var linea = CalculosTributarios.calcularLineaDetalle(extra.cantidad(), valorUnitarioSinIgv);

                DetalleFactura detalle = new DetalleFactura();
                detalle.setFactura(factura);
                detalle.setItem(itemNum++);
                detalle.setDescripcion(extra.descripcion());
                detalle.setCantidad(extra.cantidad());
                detalle.setUnidadMedida(extra.unidadMedida() != null ? extra.unidadMedida() : "ZZ");
                detalle.setValorUnitario(linea.valorUnitario().doubleValue());
                detalle.setPrecioUnitario(linea.precioUnitario().doubleValue());
                detalle.setIgv(linea.igv().doubleValue());
                detalle.setPorcentajeIgv(10.5);
                detalle.setValorTotal(linea.valorTotal().doubleValue());
                detalle.setImporteTotal(linea.importeTotal().doubleValue());
                detalles.add(detalle);
            }
        }

        if (detalles.isEmpty()) {
            throw new ExcepcionEmpresarial("La factura debe tener al menos un item");
        }

        var totales = CalculosTributarios.sumarTotalesLineas(
                detalles.stream().map(d -> new CalculosTributarios.LineaCalculada(
                        java.math.BigDecimal.valueOf(d.getValorUnitario()),
                        java.math.BigDecimal.valueOf(d.getPrecioUnitario()),
                        java.math.BigDecimal.valueOf(d.getValorTotal()),
                        java.math.BigDecimal.valueOf(d.getIgv()),
                        java.math.BigDecimal.valueOf(d.getPorcentajeIgv()),
                        java.math.BigDecimal.valueOf(d.getImporteTotal())
                )).toList()
        );

        factura.setOpGravadas(totales.opGravadas().doubleValue());
        factura.setIgv(totales.igv().doubleValue());
        factura.setTotal(totales.total().doubleValue());
        factura.setEstado(EstadoFactura.BORRADOR);
        facturaRepository.save(factura);
        detalleFacturaRepository.saveAll(detalles);

        try {
            String nombreBase = numeracionService.nombreArchivoSunat(
                    emisor.getRuc(),
                    factura.getTipoComprobante().getCodigoSunat(),
                    factura.getSerieCodigo(),
                    factura.getCorrelativo());

            String xmlStr = xmlUblGenerator.generarXml(factura, detalles, emisor);
            factura.setNombreXml(nombreBase + ".xml");

            byte[] xmlFirmado = firmaDigitalService.firmarXml(
                    xmlStr.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    nombreBase);

            String rucCert = firmaDigitalService.extraerRucDelCertificado();
            String usuarioSol = emisor.getUsuarioSol() != null ? emisor.getUsuarioSol() : "MODDATOS";
            String claveSol = emisor.getClaveSol() != null ? emisor.getClaveSol() : "MODDATOS";
            String rucEmisor = rucCert != null ? rucCert : emisor.getRuc();

            var resultado = sunatSoapClient.enviarComprobante(
                    xmlFirmado, nombreBase, rucEmisor, usuarioSol, claveSol);

            factura.setCodigoSunat(resultado.codigo());
            factura.setMensajeSunat(resultado.mensaje());

            if ("ACEPTADO".equals(resultado.identificador())) {
                factura.setEstado(EstadoFactura.ACEPTADO);
            } else {
                factura.setEstado(EstadoFactura.RECHAZADO);
            }
            factura.setXmlFirmado(nombreBase + ".xml");
            if (resultado.cdrFile() != null) {
                factura.setCdrBase64(resultado.cdrFile());
            }
        } catch (Exception e) {
            log.error("Error en envío a SUNAT: {}", e.getMessage(), e);
            factura.setEstado(EstadoFactura.BORRADOR);
            factura.setMensajeSunat("Error: " + e.getMessage());
        }

        facturaRepository.save(factura);

        notificacionResolver.emitiNotificacion(
                "Factura " + factura.getSerieCodigo() + "-" + String.format("%08d", factura.getCorrelativo()) +
                        " (" + factura.getEstado() + ")",
                "FACTURACION");

        return toFacturaResponse(factura);
    }

    @Override
    public FacturaResponse buscarFacturaPorId(Long id) {
        return toFacturaResponse(facturaRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Factura no encontrada: " + id)));
    }

    @Override
    public FacturaResponse buscarFacturaPorNumero(String serie, Integer correlativo) {
        return toFacturaResponse(facturaRepository.findBySerieCodigoAndCorrelativo(serie, correlativo)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Factura no encontrada: " + serie + "-" + correlativo)));
    }

    @Override
    public List<FacturaResponse> listarFacturasPorReserva(Long reservaId) {
        return facturaRepository.findByReservaId(reservaId).stream().map(this::toFacturaResponse).toList();
    }

    @Override
    public List<FacturaResponse> listarFacturasPorHuesped(Long huespedId) {
        return facturaRepository.findByHuespedId(huespedId).stream().map(this::toFacturaResponse).toList();
    }

    @Override
    public List<DetalleResponse> listarDetalleFactura(Long facturaId) {
        return detalleFacturaRepository.findByFacturaId(facturaId).stream()
                .map(this::toDetalleResponse).toList();
    }

    @Override
    public FacturaPage listarFacturasPaginadas(int page, int size, TipoComprobante tipo, EstadoFactura estado, LocalDate fechaInicio, LocalDate fechaFin) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDate inicio = fechaInicio != null ? fechaInicio : LocalDate.of(1900, 1, 1);
        LocalDate fin = fechaFin != null ? fechaFin : LocalDate.of(9999, 12, 31);
        Page<Factura> pageResult = facturaRepository.findAllWithFilters(tipo, estado, inicio, fin, pageable);
        List<FacturaResponse> items = pageResult.getContent().stream()
                .map(this::toFacturaResponse).toList();
        return new FacturaPage(items, pageResult.getTotalElements(),
                pageResult.getTotalPages(), pageResult.getNumber());
    }

    private EmisorResponse toEmisorResponse(Emisor e) {
        return new EmisorResponse(
                e.getId(), e.getRuc(), e.getRazonSocial(), e.getNombreComercial(),
                e.getDireccion(), e.getUbigeo(), e.getDepartamento(), e.getProvincia(),
                e.getDistrito(), e.getPorcentajeIgv(), e.getActivo());
    }

    private SerieResponse toSerieResponse(Serie s) {
        return new SerieResponse(s.getId(), s.getSerie(), s.getCorrelativo(), s.getTipoComprobante(), s.getActivo());
    }

    private DetalleResponse toDetalleResponse(DetalleFactura d) {
        return new DetalleResponse(
                d.getId(), d.getItem(), d.getDescripcion(), d.getCantidad(),
                d.getUnidadMedida(), d.getValorUnitario(), d.getPrecioUnitario(),
                d.getIgv(), d.getPorcentajeIgv(), d.getValorTotal(), d.getImporteTotal());
    }

    private FacturaResponse toFacturaResponse(Factura f) {
        String numero = f.getSerieCodigo() + "-" + String.format("%08d", f.getCorrelativo());
        List<DetalleResponse> items = detalleFacturaRepository.findByFacturaId(f.getId())
                .stream().map(this::toDetalleResponse).toList();
        return new FacturaResponse(
                f.getId(),
                f.getEmisor().getId(), f.getEmisor().getRuc(), f.getEmisor().getRazonSocial(),
                f.getSerie().getId(), f.getSerieCodigo(), f.getCorrelativo(),
                f.getTipoComprobante(), numero,
                f.getFechaEmision(), f.getFechaVencimiento(),
                f.getClienteTipoDoc(), f.getClienteNumeroDoc(), f.getClienteRazonSocial(),
                f.getClienteDireccion(),
                f.getOpGravadas(), f.getIgv(), f.getTotal(),
                f.getNombreXml(), f.getCodigoSunat(), f.getMensajeSunat(),
                f.getEstado(),
                f.getReserva() != null ? f.getReserva().getId() : null,
                f.getHuesped() != null ? f.getHuesped().getId() : null,
                f.getMetodoPago(),
                items);
    }
}
