package com.project.FoodHub.exception.dto;

import com.project.FoodHub.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Construye una ResponseEntity para una excepción personalizada.
     *
     * @param status  el estado HTTP
     * @param message mensaje de error
     * @return la ResponseEntity con el mensaje de error
     */
    public ResponseEntity<ErrorMessage> buildResponseEntity(HttpStatus status, String message) {
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), status.value(), message);
        return ResponseEntity.status(status).body(errorMessage);
    }

    @ExceptionHandler(CategoriaNoValidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleCategoriaNoValidaException(CategoriaNoValidaException exception) {
        log.error("Categoria no valida: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CuentaNoConfirmadaException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorMessage> handleCuentaNoConfirmadaException(CuentaNoConfirmadaException exception) {
        log.error("Cuenta no confirmada: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(UsuarioNoValidoException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorMessage> handleUsuarioNoValidoException(UsuarioNoValidoException exception) {
        log.error("Usuario no valido: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ListaRecetasNulaException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorMessage> handleListaRecetasNulaException(ListaRecetasNulaException exception) {
        log.error("Lista de receta vacia: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UsuarioNoAutenticadoException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorMessage> handleUsuarioNoAutenticadoException(UsuarioNoAutenticadoException exception) {
        log.error("Usuario no autenticado: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ColegiadoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> handleColegiadoNoEncontradoException(ColegiadoNoEncontradoException exception) {
        log.error("Colegiado no encontrado: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(RecetaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> handleRecetaNoEncontradaException(RecetaNoEncontradaException exception) {
        log.error("Receta no encontrada: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CreadorNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> handleCreadorNotFoundException(CreadorNoEncontradoException exception) {
        log.error("Creador no encontrado: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(TokenNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> handleTokenNoEncontradoException(TokenNoEncontradoException exception) {
        log.error("Token no encontrado: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CorreoConfirmadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> handleCorreoConfirmadoException(CorreoConfirmadoException exception) {
        log.error("Correo ya confirmado: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(CodigoConfirmadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> handleCodigoConfirmadoException(CodigoConfirmadoException exception) {
        log.error("Codigo ya confirmado: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(TokenExpiradoException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorMessage> handleTokenExpiradoException(TokenExpiradoException exception) {
        log.error("Token expirado: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(CorreoExistenteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> handleCorreoExistenteException(CorreoExistenteException exception) {
        log.error("Correo existente: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ColegiadoNoValidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleColegiadoNoValidoException(ColegiadoNoValidoException exception) {
        log.error("Colegiado no valido: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CuentaNoCreadaException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorMessage> handleCuentaNoCreadaException(CuentaNoCreadaException exception) {
        log.error("Cuenta no creada: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorMessage> handleAuthenticationException(Exception exception) {
        log.error("Auth: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(IncorrectCredentials.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorMessage> handleIncorrectCredentialsException(IncorrectCredentials exception) {
        log.error("Credenciales incorrectas: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

}
