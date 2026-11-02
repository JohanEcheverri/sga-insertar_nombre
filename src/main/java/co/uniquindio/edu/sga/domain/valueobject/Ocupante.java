package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;

public record Ocupante(NombreCompleto nombreCompleto, DocumentoIdentidad documentoIdentidad, FechaNacimiento fechaNacimiento) {

    public Ocupante {
        if (nombreCompleto == null) throw new ReglaDominioException("Debe ingresar el nombre completo");
        if (documentoIdentidad == null) throw new ReglaDominioException("Debe ingresar el documento identidad");
        if (fechaNacimiento == null) throw new ReglaDominioException("Debe ingresar la fecha nacimiento");
    }

    public int edadEn(LocalDate fecha) {
        return fechaNacimiento.edadEn(fecha);
    }

    /** RN-06 - Es facturable si a la fecha de entrada alcanza el umbral. Los no facturables sí cuentan para la capacidad. */
    public boolean esFacturable(LocalDate fechaEntrada, int umbralEdadFacturable) {
        return edadEn(fechaEntrada) >= umbralEdadFacturable;
    }
}
