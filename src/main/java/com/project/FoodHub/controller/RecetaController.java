package com.project.FoodHub.controller;

import com.project.FoodHub.dto.ConfirmacionResponse;
import com.project.FoodHub.dto.RecetaDTOResponse;
import com.project.FoodHub.dto.RecetaRequest;
import com.project.FoodHub.dto.RecetasCategoriaResponse;
import com.project.FoodHub.enumeration.Categoria;
import com.project.FoodHub.exception.ArchivoVacioException;
import com.project.FoodHub.exception.FotoPerfilException;
import com.project.FoodHub.service.IRecetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/explorar")
@RequiredArgsConstructor
public class RecetaController {

    private final IRecetaService recetaService;

    @PostMapping("/crear")
    public ResponseEntity<ConfirmacionResponse> crearReceta(
            @Valid @RequestPart("receta") RecetaRequest recetaRequest,
            @RequestPart("imagen") MultipartFile imagen) throws FotoPerfilException {

        if (imagen == null || imagen.isEmpty()) {
            throw new ArchivoVacioException("El archivo de imagen está vacío o no se envió");
        }

        return ResponseEntity.ok(recetaService.crearReceta(recetaRequest, imagen));
    }

    @GetMapping("/recetas")
    public List<RecetasCategoriaResponse> mostrarRecetasPorCategoria(
            @RequestParam("categoria") String categoriaStr,
            @RequestParam(defaultValue = "0") int page, // Número de página
            @RequestParam(defaultValue = "6") int size) {

        Categoria categoria = Categoria.fromString(categoriaStr);
        return recetaService.mostrarRecetasPorCategoria(categoria, page, size);
    }

    @GetMapping("/{idReceta}")
    public ResponseEntity<RecetaDTOResponse> verReceta(@PathVariable("idReceta") Long idReceta) {
        RecetaDTOResponse receta = recetaService.verReceta(idReceta);
        return ResponseEntity.ok(receta);
    }

}
