package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.CanalOrigen;
import co.uniquindio.edu.sga.domain.valueobject.Dinero;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import co.uniquindio.edu.sga.domain.valueobject.Estancia;
import co.uniquindio.edu.sga.domain.valueobject.FolioId;
import co.uniquindio.edu.sga.domain.valueobject.Ocupante;
import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;
import co.uniquindio.edu.sga.domain.valueobject.Titular;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/** Agregado raíz que protege las invariantes propias de una reserva. */
public class Reserva {
    private final ReservaId id;
    private final FolioId folioId;
    private final ApartamentoId apartamentoId;
    private final Titular titular;
    private final List<Ocupante> ocupantes;
    private final Estancia estancia;
    private final String identificadorExterno;
    private final LocalTime horaEstimadaLlegada;
    private final CanalOrigen canalOrigen;
    private EstadoReserva estado;
    private final Dinero valorCongelado;
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
     * Regla de ciclo de vida: toda reserva nueva inicia en estado PENDIENTE.
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
        exigir(ocupantes != null && !ocupantes.isEmpty(), "La reserva debe tener al menos un ocupante.");
        exigir(!ocupantes.contains(null), "La lista de ocupantes no puede contener valores nulos.");
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

    /** Regla de ciclo de vida: solo una reserva pendiente puede pasar al estado CONFIRMADA. */
    public void confirmar() {
        exigir(estado == EstadoReserva.PENDIENTE, "Solo una reserva pendiente puede confirmarse.");
        estado = EstadoReserva.CONFIRMADA;
    }

    /** Regla de ciclo de vida: solo una reserva confirmada puede pasar al estado EN_CURSO. */
    public void iniciarEstancia() {
        exigir(estado == EstadoReserva.CONFIRMADA, "Solo una reserva confirmada puede iniciar su estancia.");
        estado = EstadoReserva.EN_CURSO;
    }

    /** Regla de ciclo de vida: solo una reserva en curso puede pasar al estado FINALIZADA. */
    public void finalizar() {
        exigir(estado == EstadoReserva.EN_CURSO, "Solo una reserva en curso puede finalizarse.");
        estado = EstadoReserva.FINALIZADA;
    }

    /** Regla de ciclo de vida: una reserva terminal (FINALIZADA, CANCELADA o NO_SHOW) no puede cancelarse nuevamente. */
    public void cancelar() {
        exigir(estado != null && !estado.esTerminal(), "Una reserva terminal no puede cancelarse.");
        estado = EstadoReserva.CANCELADA;
    }

    /** Regla de ciclo de vida: únicamente una reserva CONFIRMADA puede pasar al estado NO_SHOW. */
    public void marcarNoShow() {
        exigir(estado == EstadoReserva.CONFIRMADA, "Solo una reserva confirmada puede marcarse como no-show.");
        estado = EstadoReserva.NO_SHOW;
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

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }

    private static String normalizarIdentificador(String identificador) {
        if (identificador == null || identificador.isBlank()) return null;
        return identificador.trim();
    }
}
