package com.project.FoodHub.repository;

import com.project.FoodHub.dto.RecetasCategoriaResponse;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.entity.Receta;
import com.project.FoodHub.enumeration.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    @Query("SELECT new com.project.FoodHub.dto.RecetasCategoriaResponse(r.id, r.titulo, r.descripcion, r.imagen) FROM Receta r WHERE r.categoria = :categoria")
    Page<RecetasCategoriaResponse> findByCategoria(Categoria categoria, Pageable pageable);

    int countByCreador(Creador creador);
}
