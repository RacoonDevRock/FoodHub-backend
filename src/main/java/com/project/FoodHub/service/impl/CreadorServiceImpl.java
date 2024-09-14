package com.project.FoodHub.service.impl;

import com.project.FoodHub.dto.CreadorDTO;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.exception.*;
import com.project.FoodHub.mapper.CreadorMapper;
import com.project.FoodHub.repository.CreadorRepository;
import com.project.FoodHub.repository.RecetaRepository;
import com.project.FoodHub.service.ICreadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreadorServiceImpl implements ICreadorService {

    private final CreadorRepository creadorRepository;
    private final RecetaRepository recetaRepository;
    private final CreadorMapper creadorMapper;


    @Override
    public List<Creador> mostrarCreadores() {
        return creadorRepository.findAll();
    }

    @Override
    public Integer obtenerCantidadDeRecetasCreadas() {
        Long idCreador = obtenerIdCreadorAutenticado();

        Creador creador = creadorRepository.findById(idCreador)
                .orElseThrow(() -> new CreadorNoEncontradoException("Creador no encontrado con ID: " + idCreador));

        return recetaRepository.countByCreador(creador);
    }

    @Override
    public CreadorDTO verPerfil() {
        Long idCreador = obtenerIdCreadorAutenticado();

        Creador creador = creadorRepository.findById(idCreador)
                .orElseThrow(() -> new CreadorNoEncontradoException("Creador no encontrado con ID: " + idCreador));

        return creadorMapper.mapToDTO(creador);
    }

    @Override
    public Creador obtenerCreadorPorEmail(String email) {
        return creadorRepository.findCreadorByCorreoElectronico(email)
                .orElseThrow(() -> new CreadorNoEncontradoException("Usuario con email: " + email + " no encontrado"));
    }

    @Override
    public Creador obtenerCreadorPorIdentificador(String identificador) {
        return creadorRepository.findByCodigoColegiatura(identificador)
                .orElseThrow(() -> new CreadorNoEncontradoException("Usuario con identificador: " + identificador + " no encontrado"));
    }

    @Override
    @Transactional
    public Creador guardarCreador(Creador creador) {
        return creadorRepository.save(creador);
    }

    private Long obtenerIdCreadorAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validarAutenticacion(authentication);
        String email = authentication.getName();
        return obtenerCreadorPorEmail(email).getIdCreador();
    }

    private void validarAutenticacion(Authentication authentication) {
        Optional.ofNullable(authentication)
                .filter(Authentication::isAuthenticated)
                .orElseThrow(() -> new UsuarioNoAutenticadoException("Usuario no autenticado"));
    }
}
