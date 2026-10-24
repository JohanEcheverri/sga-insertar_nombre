package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;

public record RangoFechas(LocalDate inicio, LocalDate fin) {

    public RangoFechas {
        if(inicio == null) throw new ReglaDominioException("Ingrese una fecha de inicio.");
        if(fin == null) throw new ReglaDominioException("Ingrese una fecha final.");
        if(inicio.isAfter(fin)) throw new ReglaDominioException("La fecha inicial no puede ser posterior a la final.");
    }
}
