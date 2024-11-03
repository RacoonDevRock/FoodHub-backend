package com.project.FoodHub.service;

import com.project.FoodHub.dto.ConfirmacionResponse;
import com.project.FoodHub.dto.RecetaDTOResponse;
import com.project.FoodHub.dto.RecetaRequest;
import com.project.FoodHub.dto.RecetasCategoriaResponse;
import com.project.FoodHub.enumeration.Categoria;
import com.project.FoodHub.exception.FotoPerfilException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRecetaService {

    ConfirmacionResponse crearReceta(RecetaRequest recetaRequest, MultipartFile imagen) throws FotoPerfilException;

    List<RecetasCategoriaResponse> mostrarRecetasPorCategoria(Categoria categoria, int page, int size);

    RecetaDTOResponse verReceta(Long idReceta);

    String obtenerUrlImagen(Long idReceta);

}
