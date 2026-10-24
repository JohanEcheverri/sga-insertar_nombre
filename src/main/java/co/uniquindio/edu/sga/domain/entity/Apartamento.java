package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.Capacidad;
import co.uniquindio.edu.sga.domain.valueobject.Dormitorio;
import co.uniquindio.edu.sga.domain.valueobject.EstadoOperativo;

import java.util.List;

/** Agregado raíz que conserva las invariantes propias de un apartamento. */
public class Apartamento {
    private final ApartamentoId id;
    private final String identificadorUnico;
    private final String descripcion;
    private final Capacidad capacidad;
    private EstadoOperativo estadoOperativo;
    private final List<Dormitorio> dormitorios;
    private boolean disponible;

    private Apartamento(Capacidad capacidad, String descripcion, List<Dormitorio> dormitorios,
                        EstadoOperativo estadoOperativo, ApartamentoId id,
                        String identificadorUnico, boolean disponible) {
        this.capacidad = capacidad;
        this.descripcion = descripcion.trim();
        this.dormitorios = List.copyOf(dormitorios);
        this.estadoOperativo = estadoOperativo;
        this.disponible = disponible;
        this.id = id;
        this.identificadorUnico = identificadorUnico.trim();
    }

    /** Crea el apartamento validando sus datos obligatorios y su configuración inicial. */
    public static Apartamento crear(Capacidad capacidad, String descripcion,
                                    List<Dormitorio> dormitorios, EstadoOperativo estadoOperativo,
                                    ApartamentoId id, String identificadorUnico, boolean disponible) {
        exigir(capacidad != null, "El apartamento debe tener una capacidad definida.");
        exigir(descripcion != null && !descripcion.isBlank(), "La descripción del apartamento es obligatoria.");
        exigir(dormitorios != null && !dormitorios.isEmpty(), "El apartamento debe tener al menos un dormitorio.");
        exigir(!dormitorios.contains(null), "La lista de dormitorios no puede contener valores nulos.");
        exigir(estadoOperativo != null, "El apartamento debe tener un estado operativo.");
        exigir(id != null, "El apartamento debe tener identificador.");
        exigir(identificadorUnico != null && !identificadorUnico.isBlank(), "El identificador único del apartamento es obligatorio.");
        // RN-11 - Un apartamento solo puede recibir un grupo si su estado operativo es PREPARADO.
        exigir(estadoOperativo != EstadoOperativo.OCUPADO || !disponible,
                "Un apartamento OCUPADO no puede estar marcado como disponible.");
        return new Apartamento(capacidad, descripcion, dormitorios, estadoOperativo,
                id, identificadorUnico, disponible);
    }

    /**
     * RN-02 - El número total de ocupantes no puede exceder la capacidad del apartamento. Sin excepciones.
     * Devuelve true cuando el grupo cabe dentro del aforo.
     */
    public boolean capacidadPermitida(int totalOcupantes) {
        exigir(totalOcupantes >= 0, "El número de ocupantes no puede ser negativo.");
        return totalOcupantes <= capacidad.aforoMax();
    }

    /** RN-11 - Un apartamento solo puede recibir un grupo si su estado operativo es PREPARADO. */
    public boolean puedeRecibirGrupo() {
        return disponible && estadoOperativo == EstadoOperativo.PREPARADO;
    }

    /** Marca como ocupado luego de que el servicio de entrega haya validado la asignación. */
    public void marcarOcupado() {
        // RN-11 - Un apartamento solo puede recibir un grupo si su estado operativo es PREPARADO.
        exigir(puedeRecibirGrupo(), "Solo un apartamento PREPARADO y disponible puede marcarse como ocupado.");
        estadoOperativo = EstadoOperativo.OCUPADO;
        disponible = false;
    }

    /** Devuelve el apartamento al estado preparado después de completar su liberación/preparación. */
    public void marcarPreparado() {
        exigir(estadoOperativo == EstadoOperativo.OCUPADO
                        || estadoOperativo == EstadoOperativo.EN_PREPARACION
                        || estadoOperativo == EstadoOperativo.PENDIENTE_PREPARACION,
                "Solo un apartamento ocupado o en proceso de preparación puede marcarse preparado.");
        estadoOperativo = EstadoOperativo.PREPARADO;
        disponible = true;
    }

    /** Retira de operación el apartamento; su disponibilidad debe coordinarse con servicios externos. */
    public void marcarFueraDeServicio() {
        exigir(estadoOperativo != EstadoOperativo.OCUPADO,
                "No se puede retirar de servicio un apartamento ocupado.");
        estadoOperativo = EstadoOperativo.FUERA_DE_SERVICIO;
        disponible = false;
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }

    public ApartamentoId getId() { return id; }
    public String getIdentificadorUnico() { return identificadorUnico; }
    public String getDescripcion() { return descripcion; }
    public Capacidad getCapacidad() { return capacidad; }
    public EstadoOperativo getEstadoOperativo() { return estadoOperativo; }
    public List<Dormitorio> getDormitorios() { return dormitorios; }
    public boolean isDisponible() { return disponible; }
}
