package com.hotel.backend_hotel.Facturacion;

import com.hotel.backend_hotel.Facturacion.entity.Factura;
import com.hotel.backend_hotel.Facturacion.repository.FacturaRepository;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionEmpresarial;
import com.hotel.backend_hotel.common.Excepcion.ExcepcionNoEncontrada;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/facturacion")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaRepository facturaRepository;

    @Value("${sunat.storage.dir:facturacion/storage/xmls}")
    private String storageDir;

    @GetMapping("/{id}/xml")
    public ResponseEntity<Resource> descargarXml(@PathVariable Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNoEncontrada("Factura no encontrada: " + id));

        String nombreArchivo = factura.getXmlFirmado();
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            nombreArchivo = factura.getNombreXml();
        }
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new ExcepcionEmpresarial("No hay XML firmado disponible para esta factura");
        }

        String nombreBase = nombreArchivo.endsWith(".xml") ? nombreArchivo.substring(0, nombreArchivo.length() - 4) : nombreArchivo;

        try {
            Path ruta = Paths.get(storageDir, "firmados", nombreBase + ".xml");
            if (!Files.exists(ruta)) {
                throw new ExcepcionEmpresarial("Archivo XML no encontrado en el servidor");
            }
            byte[] xmlBytes = Files.readAllBytes(ruta);
            ByteArrayResource resource = new ByteArrayResource(xmlBytes);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .header("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"")
                    .body(resource);
        } catch (ExcepcionEmpresarial e) {
            throw e;
        } catch (Exception e) {
            throw new ExcepcionEmpresarial("Error al leer el archivo XML: " + e.getMessage());
        }
    }
}
