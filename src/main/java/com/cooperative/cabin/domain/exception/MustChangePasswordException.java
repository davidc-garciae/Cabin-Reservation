package com.cooperative.cabin.domain.exception;

/**
 * Excepción lanzada cuando un usuario debe cambiar su contraseña
 * antes de poder continuar usando la aplicación.
 * 
 * Se retorna un código HTTP 403 (Forbidden) con información específica.
 */
public class MustChangePasswordException extends RuntimeException {

    public MustChangePasswordException(String message) {
        super(message);
    }

    public MustChangePasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
