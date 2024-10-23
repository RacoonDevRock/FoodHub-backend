package com.project.FoodHub.service;

import com.project.FoodHub.dto.ConfirmacionResponse;
import com.project.FoodHub.dto.RecetaDTOResponse;
import com.project.FoodHub.dto.RecetaRequest;
import com.project.FoodHub.dto.RecetasCategoriaResponse;
import com.project.FoodHub.enumeration.Categoria;
import com.project.FoodHub.exception.FotoPerfilException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IRecetaService {

    ConfirmacionResponse crearReceta(RecetaRequest recetaRequest, MultipartFile imagen) throws FotoPerfilException, IOException, ExecutionException, InterruptedException;

    List<RecetasCategoriaResponse> mostrarRecetasPorCategoria(Categoria categoria, int page, int size);

    RecetaDTOResponse verReceta(Long idReceta);

}
