package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.valueobject.BloqueoId;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;
import co.uniquindio.edu.sga.domain.valueobject.Estancia;

public class Bloqueo {
    // ------ Atributos Entidad Bloqueo ------ //

    private BloqueoId id;
    private ApartamentoId apartamentoId;
    private RangoFechas rangoFechas;
    private String motivo;
    private boolean activo;


    // CONSIGUELE MEJORES NOMBRES A ESTOS METODOS, NO SON MUY CLAROS
    //** RN-07 Un apartamento con bloqueo vigente sobre una noche no está disponible para esa noche. */
    public boolean estaVigenteEnRango(RangoFechas rango) {
        return activo && rangoFechas.cubre(rango);
    }

    //** RN-07 Un apartamento con bloqueo vigente sobre una noche no está disponible para esa noche. */
    public boolean chocaConEstancia(Estancia estancia) {
        return activo && rangoFechas.intersecta(estancia.rangoFechas());
    }

}
