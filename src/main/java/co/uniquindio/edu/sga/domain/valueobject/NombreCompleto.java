package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record NombreCompleto(String nombre, String apellido) {
    /*
    Método constructor de la clase
     */
    public NombreCompleto {
        if(nombre == null || nombre.isBlank()) throw new ReglaDominioException("Debe ingresar un nombre válido.");

        if(apellido == null || apellido.isBlank()) throw new ReglaDominioException("Debe ingresar un apellido válido.");
        nombre = nombre.trim();
        apellido = apellido.trim();
    }

    /*
    Obtenemos un string con formato para el nombre completo.
     */
    public String obtenerNombreCompleto() {
        return String.format("%s %s", nombre, apellido);
    }

}
