package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.time.LocalDate;

public record FechaNacimiento(LocalDate valor) {

    public FechaNacimiento{
        if(valor == null){
            throw new ReglaDominioException("Debe ingresar la fecha de nacimiento.");
        }
        if(valor.isAfter(LocalDate.now())) {
            throw new ReglaDominioException(String.format("La fecha de nacimiento [%s] es inválida porque no puede ser una fecha futura.", valor));
        }
    }
}
