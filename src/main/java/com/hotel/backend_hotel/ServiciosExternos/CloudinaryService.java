package com.hotel.backend_hotel.ServiciosExternos;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String subirImagen(MultipartFile archivo, String carpeta) throws IOException {
        Map<?, ?> resultado = cloudinary.uploader().upload(
                archivo.getBytes(),
                ObjectUtils.asMap("folder", carpeta)
        );
        return resultado.get("secure_url").toString();
    }

    public void eliminarImagen(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
