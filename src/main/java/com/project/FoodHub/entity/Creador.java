package com.project.FoodHub.entity;

import com.project.FoodHub.enumeration.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "creador")
public class Creador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_creador")
    private Long idCreador;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false)
    private String apellidoMaterno;

    @Column(name = "correo_electronico", nullable = false)
    private String correoElectronico;

    @Column(name = "contraseña", nullable = false)
    private String contrasenia;

    @Column(name = "codigo_colegiatura", nullable = false)
    private String codigoColegiatura;

    @OneToMany(mappedBy = "creador")
    private List<Receta> recetas;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "account_non_expired")
    private boolean accountNonExpired;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;

    @Enumerated(EnumType.STRING)
    private Rol role;

    @Column(name = "foto_perfil", columnDefinition = "LONGTEXT")
    private String fotoPerfil;

    public Creador(String nombre,
                   String apellidoPaterno,
                   String apellidoMaterno,
                   String email,
                   String contrasenia,
                   String codigoColegiatura) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correoElectronico = email;
        this.contrasenia = contrasenia;
        this.codigoColegiatura = codigoColegiatura;
    }
}
