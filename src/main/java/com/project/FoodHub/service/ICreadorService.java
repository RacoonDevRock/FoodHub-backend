package com.project.FoodHub.service;

import com.project.FoodHub.dto.CreadorDTO;
import com.project.FoodHub.dto.MessageResponse;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.exception.FotoPerfilException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ICreadorService {

    List<Creador> mostrarCreadores();

    Integer obtenerCantidadDeRecetasCreadas();

    CreadorDTO verPerfil();

    Creador obtenerCreadorPorEmail(String email);

    Optional<Creador> verificarCorreoRegistrado(String email);

    Creador obtenerCreadorPorIdentificador(String identificador);

    Creador guardarCreador(Creador creador);

    MessageResponse actualizarFotoPerfil(MultipartFile fotoPerfil) throws IOException, FotoPerfilException;
}
