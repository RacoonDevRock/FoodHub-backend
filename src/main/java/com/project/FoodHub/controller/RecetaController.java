package com.project.FoodHub.controller;

import com.project.FoodHub.dto.ConfirmacionResponse;
import com.project.FoodHub.dto.RecetaDTOResponse;
import com.project.FoodHub.dto.RecetasCategoriaResponse;
import com.project.FoodHub.dto.RecetaRequest;
import com.project.FoodHub.enumeration.Categoria;
import com.project.FoodHub.entity.Receta;
import com.project.FoodHub.service.IRecetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/explorar")
@RequiredArgsConstructor
public class RecetaController {

    private final IRecetaService recetaService;

    @PostMapping("/crear")
    public ResponseEntity<ConfirmacionResponse> crearReceta(
            @Valid @RequestPart("receta") RecetaRequest recetaRequest,
            @RequestPart("imagen") MultipartFile imagen) throws IOException {
        return ResponseEntity.ok(recetaService.crearReceta(recetaRequest, imagen));
    }

    @GetMapping("/recetas")
    public List<RecetasCategoriaResponse> mostrarRecetasPorCategoria(@RequestParam("categoria") String categoriaStr) {
        Categoria categoria = Categoria.fromString(categoriaStr);
        return recetaService.mostrarRecetasPorCategoria(categoria);
    }

    @GetMapping("/{idReceta}")
    public ResponseEntity<RecetaDTOResponse> verReceta(@PathVariable("idReceta") Long idReceta) {
        RecetaDTOResponse receta = recetaService.verReceta(idReceta);
        return ResponseEntity.ok(receta);
    }

}
