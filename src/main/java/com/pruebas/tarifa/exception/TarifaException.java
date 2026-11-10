package com.pruebas.tarifa.exception;

public class TarifaException extends RuntimeException {
    public TarifaException(String message, Throwable cause) {
        super(message, cause);
    }
    public TarifaException(String message) {
        super(message);
    }
}