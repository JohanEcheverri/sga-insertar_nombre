package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record DocumentoIdentidad(String numero) {

    public DocumentoIdentidad{
        
        if(numero == null || numero.isBlank() || numero.isEmpty()){
            throw new ReglaDominioException("El documento de identidad es algo obligatorio y no puede esatr vacio");
        }
        numero = numero.trim();
    }
}
