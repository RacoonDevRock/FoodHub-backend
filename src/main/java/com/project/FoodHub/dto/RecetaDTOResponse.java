package com.project.FoodHub.dto;

import com.project.FoodHub.entity.Ingrediente;
import com.project.FoodHub.entity.Instruccion;
import com.project.FoodHub.enumeration.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecetaDTOResponse {
    private String titulo;
    private String descripcion;
    private Integer tiempoCoccion;
    private Integer porciones;
    private Double calorias;
    private String imagen;
    private Categoria categoria;
    private List<Ingrediente> ingredientes;
    private List<Instruccion> instrucciones;
    private String fotoPerfil;
    private String autor;
}
