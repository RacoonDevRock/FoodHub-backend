package com.project.FoodHub.service;

import com.project.FoodHub.dto.ConfirmacionResponse;
import com.project.FoodHub.dto.RecetasCategoriaResponse;
import com.project.FoodHub.dto.RecetaRequest;
import com.project.FoodHub.entity.*;
import com.project.FoodHub.enumeration.Categoria;

import java.util.List;

public interface IRecetaService {

    ConfirmacionResponse crearReceta(RecetaRequest recetaRequest);

    void agregarIngrediente(Receta receta, Ingrediente ingrediente);

    List<RecetasCategoriaResponse> mostrarRecetasPorCategoria(Categoria categoria);

    Receta verReceta(Long idReceta);

}
