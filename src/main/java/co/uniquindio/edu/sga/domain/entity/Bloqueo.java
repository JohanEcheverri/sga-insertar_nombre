package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.valueobject.BloqueoId;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;

public class Bloqueo {
    
    // ------ Atributos Entidad Bloqueo ------ //

    private BloqueoId id;
    private ApartamentoId apartamentoId;
    private RangoFechas rangoFechas;
    private String motivo;

}
