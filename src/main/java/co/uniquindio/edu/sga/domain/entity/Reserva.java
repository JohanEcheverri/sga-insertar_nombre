package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/** Agregado raíz que protege las invariantes propias de una reserva. */
public class Reserva {
    private final ReservaId id;
    private final FolioId folioId;
    private ApartamentoId apartamentoId;
    private final Titular titular;
    private List<Ocupante> ocupantes;
    private Estancia estancia;
    private final String identificadorExterno;
    private LocalTime horaEstimadaLlegada;
    private final CanalOrigen canalOrigen;
    private EstadoReserva estado;
    // RN-22 - El valor y la política quedan congelados al crearla y solo cambian por modificación explícita.
    private Dinero valorCongelado;
    private final PoliticaCancelacion politicaCancelacion;
    private final LocalDateTime fechaCreacion;

    private Reserva(ReservaId id, FolioId folioId, ApartamentoId apartamentoId, Titular titular,
                    List<Ocupante> ocupantes, Estancia estancia, String identificadorExterno,
                    LocalTime horaEstimadaLlegada, CanalOrigen canalOrigen, Dinero valorCongelado,
                    PoliticaCancelacion politicaCancelacion, LocalDateTime fechaCreacion) {
        this.id = id;
        this.folioId = folioId;
        this.apartamentoId = apartamentoId;
        this.titular = titular;
        this.ocupantes = List.copyOf(ocupantes);
        this.estancia = estancia;
        this.identificadorExterno = normalizarIdentificador(identificadorExterno);
        this.horaEstimadaLlegada = horaEstimadaLlegada;
        this.canalOrigen = canalOrigen;
        this.estado = EstadoReserva.PENDIENTE;
        this.valorCongelado = valorCongelado;
        this.politicaCancelacion = politicaCancelacion;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Crea una reserva pendiente.
     * RN-04 - No se pueden crear reservas cuya fecha de entrada sea anterior a la fecha actual.
     * RN-22 - Valor y política congelados al crearla. Toda reserva nueva inicia en PENDIENTE.
     */
    public static Reserva crear(ReservaId id, FolioId folioId, ApartamentoId apartamentoId,
                                Titular titular, List<Ocupante> ocupantes, Estancia estancia,
                                String identificadorExterno, LocalTime horaEstimadaLlegada,
                                CanalOrigen canalOrigen, Dinero valorCongelado,
                                PoliticaCancelacion politicaCancelacion, LocalDateTime fechaCreacion) {
        exigir(id != null, "La reserva debe tener identificador.");
        exigir(folioId != null, "La reserva debe tener folio.");
        exigir(apartamentoId != null, "La reserva debe tener apartamento.");
        exigir(titular != null, "La reserva debe tener titular.");
        validarOcupantes(ocupantes);
        exigir(estancia != null, "La reserva debe tener estancia.");
        exigir(horaEstimadaLlegada != null, "Debe indicarse la hora estimada de llegada.");
        exigir(canalOrigen != null, "La reserva debe tener canal de origen.");
        exigir(valorCongelado != null, "La reserva debe congelar el valor cotizado.");
        exigir(politicaCancelacion != null, "La reserva debe conservar su política de cancelación.");
        exigir(fechaCreacion != null, "La reserva debe tener fecha de creación.");
        exigir(!estancia.fechaEntrada().isBefore(fechaCreacion.toLocalDate()),
                "RN-04 - No se pueden crear reservas cuya fecha de entrada sea anterior a la fecha actual.");
        return new Reserva(id, folioId, apartamentoId, titular, ocupantes, estancia,
                identificadorExterno, horaEstimadaLlegada, canalOrigen, valorCongelado,
                politicaCancelacion, fechaCreacion);
    }

    /** Solo una reserva pendiente puede pasar al estado CONFIRMADA. */
    public void confirmar() {
        exigir(estado == EstadoReserva.PENDIENTE, "Solo una reserva pendiente puede confirmarse.");
        estado = EstadoReserva.CONFIRMADA;
    }

    /**
     * RN-09 / RN-26 - Confirma exigiendo hora estimada de llegada y que los pagos registrados
     * cubran el anticipo establecido.
     */
    public void confirmar(BigDecimal anticipoRequerido, BigDecimal pagosRegistrados) {
        exigir(horaEstimadaLlegada != null, "RN-09 - Toda reserva debe registrar hora estimada de llegada antes de confirmarse.");
        exigir(anticipoRequerido != null && pagosRegistrados != null, "Debe indicarse el anticipo requerido y los pagos registrados.");
        exigir(pagosRegistrados.compareTo(anticipoRequerido) >= 0,
                "RN-26 - La reserva solo puede confirmarse si se cumple con el anticipo establecido ("
                        + anticipoRequerido.toPlainString() + " COP).");
        confirmar();
    }

    /** Solo una reserva confirmada puede pasar al estado EN_CURSO. */
    public void iniciarEstancia() {
        exigir(estado == EstadoReserva.CONFIRMADA, "Solo una reserva confirmada puede iniciar su estancia.");
        estado = EstadoReserva.EN_CURSO;
    }

    /** Solo una reserva en curso puede pasar al estado FINALIZADA. */
    public void finalizar() {
        exigir(estado == EstadoReserva.EN_CURSO, "Solo una reserva en curso puede finalizarse.");
        estado = EstadoReserva.FINALIZADA;
    }

    /** Sección 8 - Solo PENDIENTE o CONFIRMADA pueden cancelarse (siempre antes del registro). */
    public void cancelar() {
        exigir(estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA,
                "Solo una reserva PENDIENTE o CONFIRMADA puede cancelarse.");
        estado = EstadoReserva.CANCELADA;
    }

    /** Únicamente una reserva CONFIRMADA puede pasar al estado NO_SHOW. */
    public void marcarNoShow() {
        exigir(estado == EstadoReserva.CONFIRMADA, "Solo una reserva confirmada puede marcarse como no-show.");
        estado = EstadoReserva.NO_SHOW;
    }

    /**
     * RN-14 / RN-22 - Modificación explícita de apartamento, ocupantes, estancia u hora de llegada
     * mientras la reserva esté PENDIENTE o CONFIRMADA. El nuevo valor (ya recalculado con las
     * tarifas vigentes) reemplaza al congelado; la política congelada NO cambia.
     */
    public void modificar(ApartamentoId nuevoApartamentoId, List<Ocupante> nuevosOcupantes,
                          Estancia nuevaEstancia, LocalTime nuevaHoraLlegada, Dinero nuevoValor,
                          LocalDateTime ahora) {
        exigir(estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA,
                "Solo una reserva PENDIENTE o CONFIRMADA puede modificarse.");
        exigir(nuevoApartamentoId != null, "La reserva debe tener apartamento.");
        validarOcupantes(nuevosOcupantes);
        exigir(nuevaEstancia != null, "La reserva debe tener estancia.");
        exigir(nuevaHoraLlegada != null, "Debe indicarse la hora estimada de llegada.");
        exigir(nuevoValor != null, "La modificación debe recalcular el valor de la reserva.");
        exigir(ahora != null, "Debe indicarse el momento de la modificación.");
        exigir(!nuevaEstancia.fechaEntrada().isBefore(ahora.toLocalDate()),
                "RN-04 - No se pueden crear reservas cuya fecha de entrada sea anterior a la fecha actual.");
        this.apartamentoId = nuevoApartamentoId;
        this.ocupantes = List.copyOf(nuevosOcupantes);
        this.estancia = nuevaEstancia;
        this.horaEstimadaLlegada = nuevaHoraLlegada;
        this.valorCongelado = nuevoValor;
    }

    /** RN-21 - Una reserva PENDIENTE que supera el plazo de confirmación debe cancelarse. */
    public boolean haVencidoPlazoConfirmacion(LocalDateTime ahora, int plazoHoras) {
        return estado == EstadoReserva.PENDIENTE && ahora.isAfter(fechaCreacion.plusHours(plazoHoras));
    }

    public boolean estaActiva() {
        return estado.esActiva();
    }

    public ReservaId getId() { return id; }
    public FolioId getFolioId() { return folioId; }
    public ApartamentoId getApartamentoId() { return apartamentoId; }
    public Titular getTitular() { return titular; }
    public List<Ocupante> getOcupantes() { return ocupantes; }
    public Estancia getEstancia() { return estancia; }
    public String getIdentificadorExterno() { return identificadorExterno; }
    public LocalTime getHoraEstimadaLlegada() { return horaEstimadaLlegada; }
    public CanalOrigen getCanalOrigen() { return canalOrigen; }
    public EstadoReserva getEstado() { return estado; }
    public Dinero getValorCongelado() { return valorCongelado; }
    public PoliticaCancelacion getPoliticaCancelacion() { return politicaCancelacion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    private static void validarOcupantes(List<Ocupante> ocupantes) {
        exigir(ocupantes != null && !ocupantes.isEmpty(), "La reserva debe tener al menos un ocupante.");
        // stream en vez de contains(null): List.of(...).contains(null) lanza NullPointerException.
        exigir(ocupantes.stream().noneMatch(Objects::isNull), "La lista de ocupantes no puede contener valores nulos.");
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }

    private static String normalizarIdentificador(String identificador) {
        if (identificador == null || identificador.isBlank()) return null;
        return identificador.trim();
    }
}
