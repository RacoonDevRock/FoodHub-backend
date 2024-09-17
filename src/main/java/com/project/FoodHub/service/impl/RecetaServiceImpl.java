package com.project.FoodHub.service.impl;

import com.project.FoodHub.dto.ConfirmacionResponse;
import com.project.FoodHub.dto.RecetaRequest;
import com.project.FoodHub.dto.RecetasCategoriaResponse;
import com.project.FoodHub.entity.*;
import com.project.FoodHub.enumeration.Categoria;
import com.project.FoodHub.exception.CreadorNoEncontradoException;
import com.project.FoodHub.exception.ListaRecetasNulaException;
import com.project.FoodHub.exception.RecetaNoEncontradaException;
import com.project.FoodHub.exception.UsuarioNoAutenticadoException;
import com.project.FoodHub.repository.CreadorRepository;
import com.project.FoodHub.repository.IngredienteRepository;
import com.project.FoodHub.repository.InstruccionRepository;
import com.project.FoodHub.repository.RecetaRepository;
import com.project.FoodHub.service.ICreadorService;
import com.project.FoodHub.service.IRecetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecetaServiceImpl implements IRecetaService {

    private final RecetaRepository recetaRepository;
    private final CreadorRepository creadorRepository;
    private final IngredienteRepository ingredienteRepository;
    private final InstruccionRepository instruccionRepository;
    private final ICreadorService creadorService;

    public static final String RUTA_IMAGEN_RECETAS = "imagen_recetas/";

    @Override
    @Transactional
    public ConfirmacionResponse crearReceta(RecetaRequest recetaRequest, MultipartFile imagen) throws IOException {
        Long idCreador = obtenerIdCreadorAutenticado();

        Creador creador = creadorRepository.findById(idCreador)
                .orElseThrow(() -> new CreadorNoEncontradoException("Creador no encontrado con ID: " + idCreador));

        String nombreImagen = guardarImagen(imagen);

        Receta receta = Receta.builder()
                .titulo(recetaRequest.getTitulo())
                .descripcion(recetaRequest.getDescripcion())
                .tiempoCoccion(recetaRequest.getTiempoCoccion())
                .porciones(recetaRequest.getPorciones())
                .calorias(recetaRequest.getCalorias())
                .imagen(nombreImagen)
                .categoria(recetaRequest.getCategoria())
                .ingredientes(new ArrayList<>())
                .instrucciones(new ArrayList<>())
                .creador(creador)
                .build();

        recetaRepository.save(receta);

        for (Ingrediente ingrediente : recetaRequest.getIngredientes()) {
            agregarIngrediente(receta, ingrediente);
        }

        for (Instruccion instruccion : recetaRequest.getInstrucciones()) {
            agregarInstruccion(receta, instruccion);
        }

        return new ConfirmacionResponse("Receta creada de forma exitosa", "success");
    }

    private String guardarImagen(MultipartFile imagen) throws IOException {
        if (imagen.isEmpty()) throw new IOException("El archivo de imagen está vacío");

        String tipoArchivo = imagen.getContentType();
        if (!tipoArchivo.equals("image/jpeg") && !tipoArchivo.equals("image/png"))
            throw new IOException("El archivo no es una imagen válida");

        String nombreArchivo = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();

        Path rutaCompleta = Paths.get(RUTA_IMAGEN_RECETAS + nombreArchivo);

        Files.createDirectories(rutaCompleta.getParent());

        Files.write(rutaCompleta, imagen.getBytes());

        return nombreArchivo;
    }

    @Override
    @Transactional
    public void agregarIngrediente(Receta receta, Ingrediente ingrediente) {
        ingrediente.setReceta(receta);
        receta.getIngredientes().add(ingrediente);

        ingredienteRepository.save(ingrediente);
    }

    @Override
    @Transactional
    public List<RecetasCategoriaResponse> mostrarRecetasPorCategoria(Categoria categoria) {
        Optional<List<Receta>> recetasOptional = recetaRepository.findByCategoria(categoria);

        List<RecetasCategoriaResponse> recetasResponse = new ArrayList<>();

        List<Receta> recetas = recetasOptional.orElseThrow(() -> new ListaRecetasNulaException("La lista de recetas es nula"));

        for (Receta receta : recetas) {
            RecetasCategoriaResponse recetasCategoriaResponse = RecetasCategoriaResponse.builder()
                    .id(receta.getId())
                    .titulo(receta.getTitulo())
                    .descripcion(receta.getDescripcion())
                    .imagenReceta(receta.getImagen())
                    .build();
            recetasResponse.add(recetasCategoriaResponse);
        }

        return recetasResponse;
    }

    @Override
    public Receta verReceta(Long idReceta) {
        return recetaRepository.findById(idReceta)
                .orElseThrow(() -> new RecetaNoEncontradaException("Receta no encontrada"));
    }

    private void agregarInstruccion(Receta receta, Instruccion instruccion) {
        instruccion.setReceta(receta);
        receta.getInstrucciones().add(instruccion);

        instruccionRepository.save(instruccion);
    }

    private Long obtenerIdCreadorAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validarAutenticacion(authentication);
        String email = authentication.getName();
        return creadorService.obtenerCreadorPorEmail(email).getIdCreador();
    }

    private void validarAutenticacion(Authentication authentication) {
        Optional.ofNullable(authentication)
                .filter(Authentication::isAuthenticated)
                .orElseThrow(() -> new UsuarioNoAutenticadoException("Usuario no autenticado"));
    }

}
