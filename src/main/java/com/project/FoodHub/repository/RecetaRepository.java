package com.project.FoodHub.repository;

import com.project.FoodHub.enumeration.Categoria;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    Optional<List<Receta>> findByCategoria(Categoria categoria);

    int countByCreador(Creador creador);
}
