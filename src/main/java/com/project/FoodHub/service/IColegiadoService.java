package com.project.FoodHub.service;

public interface IColegiadoService {

    boolean validarColegiado(String nombre,
                             String apellidoPaterno,
                             String apellidoMaterno,
                             String codigoColegiado);

    boolean isCuentaConfirmada(String codigoColegiado);

    void confirmarCuenta(String codigoColegiado);

    void actualizarCuentaConfirmada(String codigoColegiatura);
}
