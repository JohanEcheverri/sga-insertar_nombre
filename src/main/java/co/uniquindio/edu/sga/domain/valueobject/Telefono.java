package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record Telefono(String numeroTelefono) {
    public Telefono{
        if(numeroTelefono == null || numeroTelefono.isBlank()) throw new ReglaDominioException("Ingrese un numero de telefono.");
        numeroTelefono = numeroTelefono.trim();
    }
}
