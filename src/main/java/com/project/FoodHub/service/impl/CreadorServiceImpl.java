package com.project.FoodHub.service.impl;

import com.project.FoodHub.dto.CreadorDTO;
import com.project.FoodHub.dto.MessageResponse;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.exception.*;
import com.project.FoodHub.repository.CreadorRepository;
import com.project.FoodHub.repository.RecetaRepository;
import com.project.FoodHub.service.ICreadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreadorServiceImpl implements ICreadorService {

    private final CreadorRepository creadorRepository;
    private final RecetaRepository recetaRepository;

    public static final String RUTA_IMAGENES = "imagenes/";

    @Override
    public List<Creador> mostrarCreadores() {
        return creadorRepository.findAll();
    }

    @Override
    public Integer obtenerCantidadDeRecetasCreadas() {
        Long idCreador = obtenerIdCreadorAutenticado();

        Creador creador = creadorRepository.findById(idCreador)
                .orElseThrow(() -> new CreadorNoEncontradoException("Creador no encontrado con ID: " + idCreador));

        return recetaRepository.countByCreador(creador);
    }

    @Override
    public CreadorDTO verPerfil() {
        Long idCreador = obtenerIdCreadorAutenticado();

        Creador creador = creadorRepository.findById(idCreador)
                .orElseThrow(() -> new CreadorNoEncontradoException("Creador no encontrado con ID: " + idCreador));

        return new CreadorDTO(creador.getNombre(),
                creador.getApellidoPaterno(),
                creador.getApellidoMaterno(),
                creador.getCorreoElectronico(),
                creador.getCodigoColegiatura(),
                creador.getFotoPerfil());
    }

    @Override
    public Creador obtenerCreadorPorEmail(String email) {
        return creadorRepository.findCreadorByCorreoElectronico(email)
                .orElseThrow(() -> new CreadorNoEncontradoException("Usuario no registrado"));
    }

    @Override
    public Optional<Creador> verificarCorreoRegistrado(String email) {
        return creadorRepository.findCreadorByCorreoElectronico(email);
    }

    @Override
    public Creador obtenerCreadorPorIdentificador(String identificador) {
        return creadorRepository.findByCodigoColegiatura(identificador)
                .orElseThrow(() -> new CreadorNoEncontradoException("Usuario con identificador: " + identificador + " no encontrado"));
    }

    @Override
    @Transactional
    public Creador guardarCreador(Creador creador) {
        return creadorRepository.save(creador);
    }

    @Override
    @Transactional
    public MessageResponse actualizarFotoPerfil(MultipartFile fotoPerfil) throws IOException, FotoPerfilException {
        if (fotoPerfil.isEmpty()) throw new IOException("El archivo de imagen está vacío");

        String tipoArchivo = fotoPerfil.getContentType();
        if (!tipoArchivo.equals("image/jpeg") && !tipoArchivo.equals("image/png") && !tipoArchivo.equals("image/jpg"))
            throw new IOException("El archivo no es una foto válida");

        String nombreArchivo = UUID.randomUUID().toString() + "_" + fotoPerfil.getOriginalFilename();
        Path rutaCompleta = Paths.get(RUTA_IMAGENES + nombreArchivo);

        try {
            Files.createDirectory(rutaCompleta.getParent());
            Files.write(rutaCompleta, fotoPerfil.getBytes());
        } catch (IOException e) {
            throw new FotoPerfilException("Error al guardar la foto", e);
        }

        Long idCreador = obtenerIdCreadorAutenticado();
        Creador creador = creadorRepository.findById(idCreador)
                .orElseThrow(() -> new CreadorNoEncontradoException("Creador no encontrado"));

        creador.setFotoPerfil(nombreArchivo);
        creadorRepository.save(creador); // Guarda el cambio en la base de datos

        return new MessageResponse("Foto actualizada correctamente.");
    }

    private Long obtenerIdCreadorAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validarAutenticacion(authentication);
        String email = authentication.getName();
        return obtenerCreadorPorEmail(email).getIdCreador();
    }

    private void validarAutenticacion(Authentication authentication) {
        Optional.ofNullable(authentication)
                .filter(Authentication::isAuthenticated)
                .orElseThrow(() -> new UsuarioNoAutenticadoException("Usuario no autenticado"));
    }
}
