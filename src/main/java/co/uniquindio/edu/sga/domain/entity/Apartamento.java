package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.Capacidad;
import co.uniquindio.edu.sga.domain.valueobject.Dormitorio;
import co.uniquindio.edu.sga.domain.valueobject.EstadoOperativo;

import java.util.List;
import java.util.Objects;

/** Agregado raíz que conserva las invariantes propias de un apartamento. */
public class Apartamento {
    private final ApartamentoId id;
    private final String identificadorUnico;
    private String descripcion;
    private Capacidad capacidad;
    private EstadoOperativo estadoOperativo;
    private List<Dormitorio> dormitorios;
    private boolean disponible;
    /** Eliminación lógica (7.3): un apartamento retirado no se vende ni aparece en listados. */
    private boolean activo = true;

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

    public static Apartamento crear(Capacidad capacidad, String descripcion,
                                    List<Dormitorio> dormitorios, EstadoOperativo estadoOperativo,
                                    ApartamentoId id, String identificadorUnico, boolean disponible) {
        exigir(capacidad != null, "El apartamento debe tener una capacidad definida.");
        exigir(descripcion != null && !descripcion.isBlank(), "La descripción del apartamento es obligatoria.");
        validarDormitorios(dormitorios);
        exigir(estadoOperativo != null, "El apartamento debe tener un estado operativo.");
        exigir(id != null, "El apartamento debe tener identificador.");
        exigir(identificadorUnico != null && !identificadorUnico.isBlank(), "El identificador único del apartamento es obligatorio.");
        exigir(estadoOperativo != EstadoOperativo.OCUPADO || !disponible,
                "Un apartamento OCUPADO no puede estar marcado como disponible.");
        return new Apartamento(capacidad, descripcion, dormitorios, estadoOperativo,
                id, identificadorUnico, disponible);
    }

    /**
     * 7.3 - Actualiza características. Cambiar la capacidad NO afecta las reservas ya creadas
     * (la advertencia al administrador la produce el caso de uso).
     */
    public void actualizarDatos(Capacidad nuevaCapacidad, String nuevaDescripcion,
                                List<Dormitorio> nuevosDormitorios) {
        exigir(activo, "No se puede modificar un apartamento retirado.");
        exigir(nuevaCapacidad != null, "El apartamento debe tener una capacidad definida.");
        exigir(nuevaDescripcion != null && !nuevaDescripcion.isBlank(), "La descripción del apartamento es obligatoria.");
        validarDormitorios(nuevosDormitorios);
        this.capacidad = nuevaCapacidad;
        this.descripcion = nuevaDescripcion.trim();
        this.dormitorios = List.copyOf(nuevosDormitorios);
    }

    /** Eliminación lógica. La verificación de reservas activas/futuras la hace el caso de uso (necesita repositorio). */
    public void retirar() {
        exigir(activo, "El apartamento ya está retirado.");
        exigir(estadoOperativo != EstadoOperativo.OCUPADO, "No se puede retirar un apartamento ocupado.");
        activo = false;
        disponible = false;
    }

    /** RN-02 - El número total de ocupantes no puede exceder la capacidad. Sin excepciones. */
    public boolean capacidadPermitida(int totalOcupantes) {
        exigir(totalOcupantes >= 0, "El número de ocupantes no puede ser negativo.");
        return totalOcupantes <= capacidad.aforoMax();
    }

    /** RN-11 - Solo puede recibir un grupo si su estado operativo es PREPARADO. */
    public boolean puedeRecibirGrupo() {
        return activo && disponible && estadoOperativo == EstadoOperativo.PREPARADO;
    }

    /** PREPARADO → OCUPADO (registro). */
    public void marcarOcupado() {
        exigir(puedeRecibirGrupo(), "Solo un apartamento PREPARADO y disponible puede marcarse como ocupado.");
        estadoOperativo = EstadoOperativo.OCUPADO;
        disponible = false;
    }

    /** OCUPADO → PENDIENTE_PREPARACION (salida del grupo). */
    public void marcarPendientePreparacion() {
        exigir(estadoOperativo == EstadoOperativo.OCUPADO,
                "Solo un apartamento ocupado puede quedar pendiente de preparación.");
        estadoOperativo = EstadoOperativo.PENDIENTE_PREPARACION;
        disponible = false;
    }

    /** PENDIENTE_PREPARACION → EN_PREPARACION (personal de servicio). */
    public void iniciarPreparacion() {
        exigir(estadoOperativo == EstadoOperativo.PENDIENTE_PREPARACION,
                "Solo un apartamento pendiente de preparación puede pasar a en preparación.");
        estadoOperativo = EstadoOperativo.EN_PREPARACION;
    }

    /** → PREPARADO desde pendiente, en preparación o (reincorporación por el administrador) fuera de servicio. */
    public void marcarPreparado() {
        exigir(estadoOperativo == EstadoOperativo.EN_PREPARACION
                        || estadoOperativo == EstadoOperativo.PENDIENTE_PREPARACION
                        || estadoOperativo == EstadoOperativo.FUERA_DE_SERVICIO,
                "Solo un apartamento en proceso de preparación (o fuera de servicio) puede marcarse preparado.");
        estadoOperativo = EstadoOperativo.PREPARADO;
        disponible = true;
    }

    /** Desde cualquier estado no ocupado; solo el administrador (restricción de rol en el caso de uso). */
    public void marcarFueraDeServicio() {
        exigir(estadoOperativo != EstadoOperativo.OCUPADO,
                "No se puede retirar de servicio un apartamento ocupado.");
        estadoOperativo = EstadoOperativo.FUERA_DE_SERVICIO;
        disponible = false;
    }

    private static void validarDormitorios(List<Dormitorio> dormitorios) {
        exigir(dormitorios != null && !dormitorios.isEmpty(), "El apartamento debe tener al menos un dormitorio.");
        exigir(dormitorios.stream().noneMatch(Objects::isNull), "La lista de dormitorios no puede contener valores nulos.");
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
    public boolean isActivo() { return activo; }
}
