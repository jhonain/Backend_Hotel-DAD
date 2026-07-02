package com.hotel.backend_hotel.ServiciosExternos;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImagenController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload/imagen")
    public ResponseEntity<Map<String, String>> subirImagen(@RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryService.subirImagen(file, "habitaciones");
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
