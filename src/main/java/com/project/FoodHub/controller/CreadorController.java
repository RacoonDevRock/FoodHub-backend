package com.project.FoodHub.controller;

import com.project.FoodHub.dto.CreadorDTO;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.service.ICreadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/creador")
@RequiredArgsConstructor
public class CreadorController {

    private final ICreadorService creadorService;

    @GetMapping("/todos")
    public List<Creador> mostrarCreador() {
        return creadorService.mostrarCreadores();
    }

    @GetMapping("/cantidadRecetas")
    public ResponseEntity<?> obtenerCantidadRecetasCreadas() {
        Integer cantidadRecetas = creadorService.obtenerCantidadDeRecetasCreadas();

        return ResponseEntity.ok(cantidadRecetas);
    }

    @GetMapping("/perfil")
    public ResponseEntity<CreadorDTO> verPerfil() {
        CreadorDTO response = creadorService.verPerfil();
        return ResponseEntity.ok(response);
    }
}
