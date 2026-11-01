package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.time.Period;

public record Ocupante(NombreCompleto nombreCompleto, DocumentoIdentidad documentoIdentidad, FechaNacimiento fechaNacimiento) {

    public Ocupante {
        if (nombreCompleto == null) throw new ReglaDominioException("Debe ingresar el nombre completo");
        if (documentoIdentidad == null) throw new ReglaDominioException("Debe ingresar el documento identidad");
        if (fechaNacimiento == null) throw new ReglaDominioException("Debe ingresar la fecha nacimiento");
    }

    /** Determina si el ocupante supera o alcanza la edad facturable en la fecha de entrada de la estancia. */
    public boolean esFacturableEn(Estancia estancia, int umbralEdadFacturable) {
        int edad = Period.between(fechaNacimiento.valor(), estancia.fechaEntrada()).getYears();
        return edad >= umbralEdadFacturable;
    }
}