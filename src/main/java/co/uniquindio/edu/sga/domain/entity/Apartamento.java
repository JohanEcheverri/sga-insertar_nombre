package co.uniquindio.edu.sga.domain.entity;

import java.util.List;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.Capacidad;
import co.uniquindio.edu.sga.domain.valueobject.EstadoOperativo;
import co.uniquindio.edu.sga.domain.valueobject.Dormitorio;

public class Apartamento {

    // ------ Atributos Entidad Apartamento ------ //

    private ApartamentoId id;
    private String identificadorUnico, nombre, descripcion;
    private Capacidad capacidad;
    private EstadoOperativo estadoOperativo;
    private List<Dormitorio> dormitorios;

}
