package com.project.FoodHub.service;

import com.project.FoodHub.dto.CreadorDTO;
import com.project.FoodHub.dto.MessageResponse;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.exception.FotoPerfilException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface ICreadorService {

    Integer obtenerCantidadDeRecetasCreadas();

    CreadorDTO verPerfil();

    Creador obtenerCreadorPorEmail(String email);

    Optional<Creador> verificarCorreoRegistrado(String email);

    Creador obtenerCreadorPorIdentificador(String identificador);

    Creador guardarCreador(Creador creador);

    MessageResponse actualizarFotoPerfil(MultipartFile fotoPerfil) throws FotoPerfilException, IOException, ExecutionException, InterruptedException;

    void eliminarCreadorPorEmail(String correoElectronico);

    Creador obtenerCreadorPorTokenConfirmacion(String tokenTemporal);

    Long obtenerIdCreadorAutenticado();

    Creador obtenerCreadorPorId(Long id);
}
