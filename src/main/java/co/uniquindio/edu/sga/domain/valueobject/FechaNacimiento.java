package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;
import java.time.Period;

public record FechaNacimiento(LocalDate valor) {

    public FechaNacimiento {
        if (valor == null) {
            throw new ReglaDominioException("Debe ingresar la fecha de nacimiento.");
        }
        if (valor.isAfter(LocalDate.now())) {
            throw new ReglaDominioException(String.format("La fecha de nacimiento [%s] es inválida porque no puede ser una fecha futura.", valor));
        }
    }

    /** 3.2 - La edad se calcula, nunca se almacena. */
    public int edadEn(LocalDate fecha) {
        if (fecha == null) {
            throw new ReglaDominioException("La fecha de referencia no puede ser nula.");
        }
        return Period.between(valor, fecha).getYears();
    }
}
