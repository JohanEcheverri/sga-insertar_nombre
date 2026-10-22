package co.uniquindio.edu.sga.domain.valueobject;

import java.util.regex.Pattern;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record CorreoElectronico(String direccion) {

    // Patrón Regex estándar para correos electrónicos
    private static final Pattern PATRON_EMAIL = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public CorreoElectronico {
        if (direccion == null || direccion.isBlank()) {
            throw new ReglaDominioException("La dirección de correo electrónico no puede estar vacía.");
        }
                
        direccion = direccion.trim();

        if (!PATRON_EMAIL.matcher(direccion).matches()) {
            throw new ReglaDominioException("El formato del correo electrónico es inválido: " + direccion);
        }
    }

    public static CorreoElectronico of(String direccion) {
        return new CorreoElectronico(direccion);
    }

    @Override
    public String toString() {
        return direccion;
    }
}
