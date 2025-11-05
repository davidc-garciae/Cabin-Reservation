package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.User;

/**
 * Validador de contraseñas según el rol del usuario.
 * 
 * Reglas:
 * - ADMIN: Mínimo 6 caracteres, máximo 50. No puede ser PIN de 4 dígitos.
 * - PROFESSOR/RETIREE: Puede usar PIN de 4 dígitos O contraseña de 6-50 caracteres.
 */
public class PasswordValidator {

    /**
     * Valida una contraseña según el rol del usuario.
     * 
     * @param password Contraseña a validar
     * @param role Rol del usuario
     * @throws IllegalArgumentException Si la contraseña no cumple con los requisitos
     */
    public static void validatePasswordForRole(String password, User.UserRole role) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        if (role == User.UserRole.ADMIN) {
            validateAdminPassword(password);
        } else {
            // PROFESSOR o RETIREE
            validateNormalUserPassword(password);
        }
    }

    /**
     * Valida contraseña para administradores.
     * Debe tener mínimo 6 caracteres y máximo 50.
     * No puede ser un PIN de 4 dígitos.
     */
    private static void validateAdminPassword(String password) {
        if (password.length() < 6) {
            throw new IllegalArgumentException(
                    "Administradores deben usar contraseña de al menos 6 caracteres");
        }
        
        if (password.length() > 50) {
            throw new IllegalArgumentException(
                    "La contraseña no puede exceder 50 caracteres");
        }

        // Verificar que no sea un PIN de 4 dígitos
        if (password.matches("^\\d{4}$")) {
            throw new IllegalArgumentException(
                    "Administradores no pueden usar PIN de 4 dígitos. Debe usar una contraseña de al menos 6 caracteres");
        }
    }

    /**
     * Valida contraseña para usuarios normales (PROFESSOR, RETIREE).
     * Puede ser:
     * - PIN de exactamente 4 dígitos numéricos
     * - Contraseña de 6-50 caracteres
     */
    private static void validateNormalUserPassword(String password) {
        boolean isValidPin = password.matches("^\\d{4}$");
        boolean isValidPassword = password.length() >= 6 && password.length() <= 50;

        if (!isValidPin && !isValidPassword) {
            throw new IllegalArgumentException(
                    "Use PIN de 4 dígitos o contraseña de 6-50 caracteres");
        }
    }

    /**
     * Verifica si una contraseña es un PIN de 4 dígitos.
     * 
     * @param password Contraseña a verificar
     * @return true si es un PIN de 4 dígitos, false en caso contrario
     */
    public static boolean isPin4Digits(String password) {
        return password != null && password.matches("^\\d{4}$");
    }
}
