package co.uniquindio.edu.sga.application.support;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

/** 7.2 - Contraseña segura: mínimo 8 caracteres, con mayúsculas y números. Se valida ANTES de encriptar. */
public final class PoliticaContrasenia {

    private PoliticaContrasenia() { }

    public static void validar(String contrasenia) {
        if (contrasenia == null || contrasenia.length() < 8
                || contrasenia.chars().noneMatch(Character::isUpperCase)
                || contrasenia.chars().noneMatch(Character::isDigit)) {
            throw new ReglaDominioException(
                    "La contraseña debe tener mínimo 8 caracteres, con al menos una mayúscula y un número.");
        }
    }
}
