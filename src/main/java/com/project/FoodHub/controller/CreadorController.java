package com.project.FoodHub.controller;

import com.project.FoodHub.dto.CreadorDTO;
import com.project.FoodHub.dto.MessageResponse;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.exception.FotoPerfilException;
import com.project.FoodHub.service.ICreadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/creador")
@RequiredArgsConstructor
public class CreadorController {

    private final ICreadorService creadorService;

    @GetMapping
    public ResponseEntity<List<Creador>> mostrarCreadores() {
        List<Creador> creadores = creadorService.mostrarCreadores();
        return ResponseEntity.ok(creadores);
    }

    @GetMapping("/cantidadRecetas")
    public ResponseEntity<Integer> obtenerCantidadRecetasCreadas() {
        Integer cantidadRecetas = creadorService.obtenerCantidadDeRecetasCreadas();
        return ResponseEntity.ok(cantidadRecetas);
    }

    @GetMapping("/perfil")
    public ResponseEntity<CreadorDTO> verPerfil() {
        CreadorDTO perfil = creadorService.verPerfil();
        return ResponseEntity.ok(perfil);
    }

    @PostMapping("/actualizarFotoPerfil")
    public ResponseEntity<MessageResponse> actualizarFotoPerfil(@RequestPart("fotoPerfil") MultipartFile fotoPerfil) throws IOException, FotoPerfilException {
        MessageResponse response = creadorService.actualizarFotoPerfil(fotoPerfil);
        return ResponseEntity.ok(response);
    }
}
