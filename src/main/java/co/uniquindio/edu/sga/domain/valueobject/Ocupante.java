package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record Ocupante(NombreCompleto nombreCompleto, DocumentoIdentidad documentoIdentidad, FechaNacimiento fechaNacimiento) {

    public Ocupante{
        if(nombreCompleto == null) throw new ReglaDominioException("Debe ingresar el nombre completo");
        if(documentoIdentidad==null) throw new ReglaDominioException("Debe ingresar el documento identidad");
        if(fechaNacimiento == null) throw new ReglaDominioException("Debe ingresar la fecha nacimiento");
    }

}
