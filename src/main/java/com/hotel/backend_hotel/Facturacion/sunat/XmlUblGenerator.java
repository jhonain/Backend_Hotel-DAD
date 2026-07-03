package com.hotel.backend_hotel.Facturacion.sunat;

import com.hotel.backend_hotel.Facturacion.entity.DetalleFactura;
import com.hotel.backend_hotel.Facturacion.entity.Emisor;
import com.hotel.backend_hotel.Facturacion.entity.Factura;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class XmlUblGenerator {

    private static final Logger log = LoggerFactory.getLogger(XmlUblGenerator.class);

    private final Configuration freemarkerConfig;

    public XmlUblGenerator(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public String generarXml(Factura factura, List<DetalleFactura> detalles, Emisor emisor) throws Exception {
        Template template = freemarkerConfig.getTemplate("ubl-invoice.ftl");

        String serieNum = factura.getSerieCodigo() + "-" + String.format("%08d", factura.getCorrelativo());
        String fechaEmision = factura.getFechaEmision() != null
                ? factura.getFechaEmision().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        Map<String, Object> model = new HashMap<>();
        model.put("serieNum", serieNum);
        model.put("fechaEmision", fechaEmision);
        model.put("tipoComprobante", factura.getTipoComprobante().getCodigoSunat());
        model.put("totalLetras", totalEnLetrasSoles(factura.getTotal()));
        model.put("moneda", "PEN");
        model.put("rucEmisor", emisor.getRuc());
        model.put("razonSocialEmisor", emisor.getRazonSocial());
        model.put("ubigeoEmisor", emisor.getUbigeo() != null ? emisor.getUbigeo() : "140101");
        model.put("ciudadEmisor", emisor.getProvincia() != null ? emisor.getProvincia() : "LIMA");
        model.put("departamentoEmisor", emisor.getDepartamento() != null ? emisor.getDepartamento() : "LIMA");
        model.put("direccionEmisor", emisor.getDireccion() != null ? emisor.getDireccion() : "SIN DIRECCION");
        model.put("clienteTipoDoc", factura.getClienteTipoDoc() != null ? factura.getClienteTipoDoc() : "1");
        model.put("clienteNumeroDoc", factura.getClienteNumeroDoc() != null ? factura.getClienteNumeroDoc() : "-");
        model.put("clienteRazonSocial", factura.getClienteRazonSocial() != null ? factura.getClienteRazonSocial() : "-");
        model.put("clienteDireccion", factura.getClienteDireccion() != null ? factura.getClienteDireccion() : "-");
        model.put("totalOpGravadas", factura.getOpGravadas());
        model.put("totalIgv", factura.getIgv());
        model.put("total", factura.getTotal());

        List<Map<String, Object>> detallesMap = detalles.stream().map(det -> {
            Map<String, Object> m = new HashMap<>();
            m.put("item", det.getItem());
            m.put("descripcion", det.getDescripcion() != null ? det.getDescripcion() : "Servicio");
            m.put("unidadMedida", det.getUnidadMedida() != null ? det.getUnidadMedida() : "ZZ");
            m.put("cantidad", det.getCantidad());
            m.put("valorUnitario", det.getValorUnitario());
            m.put("precioUnitario", det.getPrecioUnitario());
            m.put("igv", det.getIgv());
            m.put("porcentajeIgv", det.getPorcentajeIgv() != null ? det.getPorcentajeIgv() : 18.0);
            m.put("valorTotal", det.getValorTotal());
            m.put("importeTotal", det.getImporteTotal());
            return m;
        }).toList();

        model.put("detalles", detallesMap);

        StringWriter writer = new StringWriter();
        template.process(model, writer);
        String xml = writer.toString();
        xml = ajustarXmlSunat(xml, serieNum);
        return xml;
    }

    private String ajustarXmlSunat(String xml, String serieNum) {
        xml = xml.replaceAll(
                "<cbc:ProfileID[^>]*>0101</cbc:ProfileID>",
                "<cbc:ProfileID>0101</cbc:ProfileID>");
        xml = xml.replaceAll(
                "<cbc:DocumentCurrencyCode[^>]*>([^<]+)</cbc:DocumentCurrencyCode>",
                "<cbc:DocumentCurrencyCode>$1</cbc:DocumentCurrencyCode>");
        xml = xml.replaceAll(
                "\\s*<cbc:IssueTime>[^<]*</cbc:IssueTime>\\s*", "\n");
        xml = Pattern.compile(
                "\\s*<cbc:Note(?![^>]*languageLocaleID=\"1000\")[^>]*>.*?</cbc:Note>\\s*",
                Pattern.DOTALL).matcher(xml).replaceAll("\n");
        xml = xml.replaceAll(
                "<cbc:URI>#?[^<]+</cbc:URI>",
                "<cbc:URI>#" + serieNum.replace("-", "") + "</cbc:URI>");
        return xml;
    }

    private String totalEnLetrasSoles(Double total) {
        if (total == null) total = 0.0;
        BigDecimal monto = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
        long entero = monto.longValue();
        int centavos = monto.subtract(BigDecimal.valueOf(entero))
                .multiply(BigDecimal.valueOf(100)).intValue();

        String letras = numeroEnLetras(entero);
        return "SON " + letras + " CON " + String.format("%02d", centavos) + "/100 SOLES";
    }

    private String numeroEnLetras(long num) {
        if (num == 0) return "CERO";
        String[] unidades = {"", "UN", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE"};
        String[] especiales = {"DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISEIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE"};
        String[] decenas = {"", "", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"};
        String[] centenas = {"", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"};

        StringBuilder sb = new StringBuilder();
        long millones = num / 1_000_000;
        long miles = (num % 1_000_000) / 1_000;
        long resto = num % 1_000;

        if (millones > 0) {
            sb.append(millones == 1 ? "UN MILLON" : numeroEnLetras(millones) + " MILLONES");
            if (miles > 0 || resto > 0) sb.append(" ");
        }
        if (miles > 0) {
            if (miles == 1) sb.append("MIL");
            else sb.append(numeroEnLetras(miles) + " MIL");
            if (resto > 0) sb.append(" ");
        }
        if (resto > 0) {
            if (resto < 10) sb.append(unidades[(int) resto]);
            else if (resto < 20) sb.append(especiales[(int) resto - 10]);
            else if (resto < 100) {
                int d = (int) resto / 10;
                int u = (int) resto % 10;
                if (u == 0) sb.append(decenas[d]);
                else if (d == 2) sb.append("VEINTI" + unidades[u]);
                else sb.append(decenas[d] + " Y " + unidades[u]);
            } else {
                if (resto == 100) sb.append("CIEN");
                else {
                    int c = (int) resto / 100;
                    int r = (int) resto % 100;
                    sb.append(centenas[c]);
                    if (r > 0) sb.append(" " + numeroEnLetras(r));
                }
            }
        }
        return sb.toString().trim();
    }
}
