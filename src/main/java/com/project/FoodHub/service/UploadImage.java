package com.project.FoodHub.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.project.FoodHub.exception.FotoPerfilException;
import com.project.FoodHub.exception.ImagenNoValidaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@Slf4j
public class UploadImage {

    private static final String CREDENTIAL_PATH = "./credentials.json";

    @Value("${google.drive.folder.id}")
    private String folderId;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final Drive driveService;

    private UploadImage() throws GeneralSecurityException, IOException {
        this.driveService = createDriveService();
        log.info(CREDENTIAL_PATH);
        log.info(folderId);
    }

    private Drive createDriveService() throws GeneralSecurityException, IOException {

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(CREDENTIAL_PATH)).createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("FoodHub")
                .build();
    }

    public String guardarImagen(MultipartFile imagen) throws FotoPerfilException {

        String tipoArchivo = Optional.ofNullable(imagen.getContentType()).orElse("");
        List<String> tiposPermitidos = Arrays.asList("image/jpeg", "image/jpg", "image/png");

        if (tiposPermitidos.stream().noneMatch(tipoArchivo::equalsIgnoreCase)) {
            throw new ImagenNoValidaException("El archivo no es una imagen válida");
        }

        try {
            String nombreArchivo = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();
            nombreArchivo = nombreArchivo.replaceAll("[^a-zA-Z0-9._-]", "");

            Path rutaCompleta = Paths.get(System.getProperty("java.io.tmpdir") + nombreArchivo);
            Files.write(rutaCompleta, imagen.getBytes());

            String url = uploadImageToDrive(rutaCompleta.toFile());

            if (!Files.exists(rutaCompleta.getParent())) {
                Files.createDirectories(rutaCompleta.getParent());
            }

            return url;
        } catch (IOException e) {
            throw new FotoPerfilException("Error al guardar la foto", e);
        }
    }

    private String uploadImageToDrive(File file) throws IOException {
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(file.getName());
        fileMetadata.setParents(Collections.singletonList(folderId));

        FileContent mediaContent = new FileContent("image/jpeg", file);
        com.google.api.services.drive.model.File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
    }
}
