package co.uniquindio.edu.sga.domain.entity;

import java.util.List;
import co.uniquindio.edu.sga.domain.valueobject.FolioId;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;
import co.uniquindio.edu.sga.domain.valueobject.EstadoFolio;
import co.uniquindio.edu.sga.domain.valueobject.Cargo;
import co.uniquindio.edu.sga.domain.valueobject.Pago;
import co.uniquindio.edu.sga.domain.valueobject.AutorizacionCierre;


public class Folio {
    
    // ------ Atributos Entidad Folio ------ //

    private FolioId id;
    private ReservaId reservaId;
    private EstadoFolio estado;
    private List<Cargo> cargos;
    private List<Pago> pagos;
    private AutorizacionCierre autorizacionCierre;

}
