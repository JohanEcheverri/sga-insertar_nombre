package co.uniquindio.edu.sga.domain.entity;

import java.util.List;
import co.uniquindio.edu.sga.domain.valueobject.AlojamientoId;
import co.uniquindio.edu.sga.domain.valueobject.Ubicacion;
import co.uniquindio.edu.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;


public class Alojamiento {

    // ------ Atributos Entidad Alojamiento ------ //

    private AlojamientoId id;
    private String nombre;
    private Ubicacion ubicacion;
    private ConfiguracionAlojamiento configuracion;
    private List<Apartamento> apartamentos;
    private List<Temporada> temporadas;
    private PoliticaCancelacion politicaVigente;
    
}
