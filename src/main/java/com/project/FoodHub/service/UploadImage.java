package com.project.FoodHub.service;

import com.project.FoodHub.exception.ArchivoVacioException;
import com.project.FoodHub.exception.FotoPerfilException;
import com.project.FoodHub.exception.ImagenNoValidaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadImage {

    public static final String RUTA_IMAGENES = "imagenes/";

    public String guardarImagen(MultipartFile imagen) throws FotoPerfilException {
        if (imagen.isEmpty()) throw new ArchivoVacioException("El archivo de imagen está vacío");

        String tipoArchivo = imagen.getContentType();
        if (!tipoArchivo.equals("image/jpeg") && !tipoArchivo.equals("image/png"))
            throw new ImagenNoValidaException("El archivo no es una imagen válida");

        String nombreArchivo = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();

        Path rutaCompleta = Paths.get(RUTA_IMAGENES + nombreArchivo);

        try {
            if (!Files.exists(rutaCompleta.getParent())) {
                Files.createDirectory(rutaCompleta.getParent());
            }
            Files.write(rutaCompleta, imagen.getBytes());
        } catch (IOException e) {
            throw new FotoPerfilException("Error al guardar la foto", e);
        }

        return nombreArchivo;
    }
}
