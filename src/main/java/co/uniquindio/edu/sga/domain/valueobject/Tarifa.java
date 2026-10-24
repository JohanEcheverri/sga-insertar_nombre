package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record Tarifa(TemporadaId temporadaId, ApartamentoId apartamentoId, Dinero precioNocheOcupante) {

    public Tarifa{
        if(temporadaId == null) throw new ReglaDominioException("Ingrese un identificador de una temporada.");
        if(apartamentoId == null) throw new ReglaDominioException("Ingrese un identificador de un apartamento.");
        if(precioNocheOcupante == null) throw new ReglaDominioException("Ingrese un precio de noche por ocupante.");
    }
}
