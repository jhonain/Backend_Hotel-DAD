package com.hotel.backend_hotel.Facturacion.sunat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class SunatSoapClient {

    private static final Logger log = LoggerFactory.getLogger(SunatSoapClient.class);
    private static final String SUNAT_BETA_URL = "https://e-beta.sunat.gob.pe/ol-ti-itcpfegem-beta/billService";

    @Value("${sunat.storage.dir:storage/xmls}")
    private String storageDir;

    @Value("${sunat.modo:beta}")
    private String modoSunat;

    public record ResultadoSunat(
            String identificador,
            String codigo,
            String mensaje,
            String cdrFile
    ) {}

    public ResultadoSunat enviarComprobante(byte[] xmlFirmado, String nombreComprobante,
                                            String rucEmisor, String usuarioSol, String claveSol) {
        if ("simulado".equalsIgnoreCase(modoSunat)) {
            return simularEnvio(nombreComprobante);
        }
        return enviarSoap(xmlFirmado, nombreComprobante, rucEmisor, usuarioSol, claveSol);
    }

    private ResultadoSunat enviarSoap(byte[] xmlFirmado, String nombreComprobante,
                                      String rucEmisor, String usuarioSol, String claveSol) {
        try {
            Path firmadosDir = Paths.get(storageDir, "firmados");
            Files.createDirectories(firmadosDir);
            Files.write(firmadosDir.resolve(nombreComprobante + ".xml"), xmlFirmado);

            byte[] zipBytes = comprimirXml(xmlFirmado, nombreComprobante);
            String zipB64 = Base64.getEncoder().encodeToString(zipBytes);

            String username = rucEmisor + usuarioSol;
            String soapXml = String.format("""
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                      xmlns:ser="http://service.sunat.gob.pe"
                                      xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                        <soapenv:Header>
                            <wsse:Security>
                                <wsse:UsernameToken>
                                    <wsse:Username>%s</wsse:Username>
                                    <wsse:Password>%s</wsse:Password>
                                </wsse:UsernameToken>
                            </wsse:Security>
                        </soapenv:Header>
                        <soapenv:Body>
                            <ser:sendBill>
                                <fileName>%s.zip</fileName>
                                <contentFile>%s</contentFile>
                            </ser:sendBill>
                        </soapenv:Body>
                    </soapenv:Envelope>""", username, claveSol, nombreComprobante, zipB64);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUNAT_BETA_URL))
                    .header("Content-Type", "text/xml;charset=UTF-8")
                    .header("SOAPAction", "urn:sendBill")
                    .POST(HttpRequest.BodyPublishers.ofString(soapXml, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(60))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            guardarLogRespuesta(nombreComprobante, response.body());

            if (response.statusCode() == 200 || response.statusCode() == 500) {
                return procesarRespuestaSoap(response.body(), nombreComprobante);
            }

            return new ResultadoSunat("ERROR_SOAP", String.valueOf(response.statusCode()),
                    "Error HTTP " + response.statusCode(), "");

        } catch (Exception e) {
            log.error("Error en envío SOAP a SUNAT: {}", e.getMessage(), e);
            return new ResultadoSunat("ERROR", "9999", "Error de conexión: " + e.getMessage(), "");
        }
    }

    private byte[] comprimirXml(byte[] xmlBytes, String nombreBase) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry(nombreBase + ".xml"));
            zos.write(xmlBytes);
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    private void guardarLogRespuesta(String nombreComprobante, String respuesta) {
        try {
            Path logDir = Paths.get(storageDir, "logs");
            Files.createDirectories(logDir);
            Files.writeString(logDir.resolve("ultima_respuesta_" + nombreComprobante + ".xml"), respuesta);
        } catch (Exception e) {
            log.warn("No se pudo guardar log de respuesta SUNAT: {}", e.getMessage());
        }
    }

    private ResultadoSunat procesarRespuestaSoap(String responseXml, String nombreComprobante) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(responseXml)));

            Element cdrContent = tryFindElement(doc, "applicationResponse");
            if (cdrContent == null) cdrContent = tryFindElement(doc, "return");
            if (cdrContent == null) cdrContent = tryFindElement(doc, "content");

            if (cdrContent != null && cdrContent.getTextContent() != null
                    && cdrContent.getTextContent().length() > 100) {
                String cdrB64 = cdrContent.getTextContent().trim();
                return guardarCdr(cdrB64, nombreComprobante);
            }

            String faultString = tryFindFaultString(doc);
            if (faultString != null) {
                String faultCode = tryFindFaultCode(doc);
                return new ResultadoSunat("RECHAZADO", faultCode,
                        mensajeFaultAmigable(faultCode, faultString), "");
            }

            return new ResultadoSunat("ERROR", "99", "Respuesta SUNAT no reconocida", "");

        } catch (Exception e) {
            log.error("Error procesando respuesta SOAP: {}", e.getMessage(), e);
            return new ResultadoSunat("ERROR", "9999", "Error procesando respuesta: " + e.getMessage(), "");
        }
    }

    private ResultadoSunat guardarCdr(String cdrB64, String nombreComprobante) {
        try {
            byte[] cdrBytes = Base64.getDecoder().decode(cdrB64);
            Path cdrDir = Paths.get(storageDir, "cdrs");
            Files.createDirectories(cdrDir);

            String nombreCdr = "R-" + nombreComprobante + ".zip";
            Path rutaZip = cdrDir.resolve(nombreCdr);
            Files.write(rutaZip, cdrBytes);

            String cdrXml = extraerXmlDeZip(cdrBytes);
            if (cdrXml != null) {
                Path rutaXml = cdrDir.resolve("R-" + nombreComprobante + ".xml");
                Files.writeString(rutaXml, cdrXml);

                String[] resultado = leerCodigoCdr(cdrXml);
                String codigo = resultado[0];
                String mensaje = resultado[1];

                if ("0".equals(codigo)) {
                    return new ResultadoSunat("ACEPTADO", "0",
                            "Comprobante aceptado por SUNAT. CDR: R-" + nombreComprobante + ".xml",
                            "R-" + nombreComprobante + ".xml");
                }
                return new ResultadoSunat("RECHAZADO", codigo, mensaje != null ? mensaje : "SUNAT rechazó el comprobante",
                        "R-" + nombreComprobante + ".xml");
            }

            return new ResultadoSunat("ACEPTADO", "0",
                    "Comprobante aceptado. CDR: " + nombreCdr, nombreCdr);

        } catch (Exception e) {
            log.error("Error guardando CDR: {}", e.getMessage(), e);
            return new ResultadoSunat("ERROR", "9999", "Error procesando CDR: " + e.getMessage(), "");
        }
    }

    private String extraerXmlDeZip(byte[] zipBytes) {
        try (var zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                zis.transferTo(baos);
                String contenido = baos.toString(StandardCharsets.UTF_8).trim();
                if (!contenido.isEmpty()) return contenido;
            }
            log.warn("No se encontró contenido XML en el ZIP CDR");
            return null;
        } catch (Exception e) {
            log.error("Error extrayendo XML del CDR ZIP: {}", e.getMessage());
            return null;
        }
    }

    private String[] leerCodigoCdr(String cdrXml) {
        if (cdrXml == null || cdrXml.isBlank()) return new String[]{"99", "CDR vacio"};
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(cdrXml)));

            String codigo = "0";
            String mensaje = "";

            Element rc = tryFindElement(doc, "ResponseCode");
            if (rc != null && rc.getTextContent() != null) codigo = rc.getTextContent().trim();
            Element desc = tryFindElement(doc, "Description");
            if (desc != null && desc.getTextContent() != null) mensaje = desc.getTextContent().trim();

            return new String[]{codigo, mensaje};

        } catch (Exception e) {
            return new String[]{"0", ""};
        }
    }

    private ResultadoSunat simularEnvio(String nombreComprobante) {
        try {
            Path cdrDir = Paths.get(storageDir, "cdrs");
            Files.createDirectories(cdrDir);
            String nombreCdr = "R-" + nombreComprobante + ".xml";
            Path rutaCdr = cdrDir.resolve(nombreCdr);

            String[] partes = nombreComprobante.split("-");
            String ruc = partes.length > 0 ? partes[0] : "20123456789";
            String tipo = partes.length > 1 ? partes[1] : "01";
            String serieCorr = partes.length > 2 ? String.join("-", java.util.Arrays.copyOfRange(partes, 2, partes.length)) : "F001-00000012";

            String cdrXml = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <ApplicationResponse xmlns="urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2"
                                         xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
                                         xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2">
                        <cac:DocumentResponse>
                            <cac:Response>
                                <cbc:ResponseCode>0</cbc:ResponseCode>
                                <cbc:Description><![CDATA[Aceptado (modo simulado). %s %s]]></cbc:Description>
                            </cac:Response>
                        </cac:DocumentResponse>
                    </ApplicationResponse>""".formatted(tipo, serieCorr);

            Files.writeString(rutaCdr, cdrXml, StandardCharsets.UTF_8);

            return new ResultadoSunat("ACEPTADO", "0",
                    "Comprobante aceptado (simulado). CDR: " + nombreCdr, nombreCdr);

        } catch (Exception e) {
            return new ResultadoSunat("ERROR", "9999", e.getMessage(), "");
        }
    }

    private Element tryFindElement(Document doc, String localName) {
        var list = doc.getElementsByTagNameNS("*", localName);
        if (list.getLength() > 0) return (Element) list.item(0);
        list = doc.getElementsByTagName(localName);
        if (list.getLength() > 0) return (Element) list.item(0);
        return null;
    }

    private String tryFindFaultString(Document doc) {
        Element e = tryFindElement(doc, "faultstring");
        return e != null ? e.getTextContent() : null;
    }

    private String tryFindFaultCode(Document doc) {
        Element e = tryFindElement(doc, "faultcode");
        if (e != null && e.getTextContent() != null) {
            String[] parts = e.getTextContent().split(":");
            return parts.length > 1 ? parts[1].trim() : "99";
        }
        return "99";
    }

    private String mensajeFaultAmigable(String codigo, String fault) {
        return switch (codigo) {
            case "2074" -> "SUNAT rechazó el XML (código 2074). Revise firma, PartyTaxScheme, TaxExemptionReasonCode. Detalle: " + fault;
            case "2335" -> "SUNAT: firma digital inválida (2335). Detalle: " + fault;
            case "0306" -> "SUNAT XML malformado (0306). Detalle: " + fault;
            case "3205" -> "SUNAT: falta tipo de operación (cat. 51). Detalle: " + fault;
            case "2800" -> "SUNAT: tipo documento receptor no permitido para factura. Use RUC. Detalle: " + fault;
            default -> "SUNAT: " + fault;
        };
    }
}
