package com.project.FoodHub.repository;

import com.project.FoodHub.entity.Creador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreadorRepository extends JpaRepository<Creador, Long> {

    @Modifying
    @Query("UPDATE Creador t SET t.isEnabled = true WHERE t.correoElectronico = :correo")
    int enableUser(String correo);

    Optional<Creador> findByCodigoColegiatura(String identificador);
    Optional<Creador> findCreadorByCorreoElectronico(String correoElectronico);
    Optional<Creador> findCreadorByTokenConfirmacion(String tokenConfirmacion);

    @Query("SELECT c FROM Creador c JOIN c.recetas r WHERE r.id = :recetaId")
    Optional<Creador> findCreadorByRecetaId(Long recetaId);
}
