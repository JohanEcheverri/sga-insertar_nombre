package co.uniquindio.edu.sga.domain.entity;


import co.uniquindio.edu.sga.domain.valueobject.NovedadId;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.GravedadNovedad;
import java.time.LocalDateTime;

public class Novedad {

    // ------ Atributos Entidad Novedad ------ //

    private NovedadId id;
    private ApartamentoId apartamentoId;
    private String autor, descripcion;
    private GravedadNovedad gravedad;
    private LocalDateTime fechaReporte;
}
