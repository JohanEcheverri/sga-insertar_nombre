package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.GravedadNovedad;
import co.uniquindio.edu.sga.domain.valueobject.NovedadId;

import java.time.LocalDateTime;

/** Entidad del dominio que representa un reporte asociado a un apartamento. */
public class Novedad {
    private final NovedadId id;
    private final ApartamentoId apartamentoId;
    private final String autor;
    private final String descripcion;
    private final GravedadNovedad gravedad;
    private final LocalDateTime fechaReporte;

    private Novedad(NovedadId id, ApartamentoId apartamentoId, String autor,
                    String descripcion, GravedadNovedad gravedad, LocalDateTime fechaReporte) {
        this.id = id;
        this.apartamentoId = apartamentoId;
        this.autor = autor;
        this.descripcion = descripcion;
        this.gravedad = gravedad;
        this.fechaReporte = fechaReporte;
    }

    /**
     * Crea una novedad con sus datos completos.
     * Regla de creación: cada reporte debe identificar la novedad, el apartamento,
     * el autor, una descripción, su gravedad y el momento del reporte.
     */
    public static Novedad crear(NovedadId id, ApartamentoId apartamentoId, String autor,
                                String descripcion, GravedadNovedad gravedad,
                                LocalDateTime fechaReporte) {
        exigir(id != null, "La novedad debe tener identificador.");
        exigir(apartamentoId != null, "La novedad debe estar asociada a un apartamento.");
        exigir(autor != null && !autor.isBlank(), "La novedad debe identificar a su autor.");
        exigir(descripcion != null && !descripcion.isBlank(), "La novedad debe tener descripción.");
        exigir(gravedad != null, "La novedad debe tener un nivel de gravedad.");
        exigir(fechaReporte != null, "La novedad debe registrar fecha y hora de reporte.");
        return new Novedad(id, apartamentoId, autor.trim(), descripcion.trim(), gravedad, fechaReporte);
    }

    /** Atajo de creación que genera identificador y fecha con el reloj del sistema. */
    public static Novedad reportar(ApartamentoId apartamentoId, String autor,
                                   String descripcion, GravedadNovedad gravedad) {
        return crear(NovedadId.generar(), apartamentoId, autor, descripcion, gravedad,
                LocalDateTime.now());
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }

    public NovedadId getId() { return id; }
    public ApartamentoId getApartamentoId() { return apartamentoId; }
    public String getAutor() { return autor; }
    public String getDescripcion() { return descripcion; }
    public GravedadNovedad getGravedad() { return gravedad; }
    public LocalDateTime getFechaReporte() { return fechaReporte; }
}
