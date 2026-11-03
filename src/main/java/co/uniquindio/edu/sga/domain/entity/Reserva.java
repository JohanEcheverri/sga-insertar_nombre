package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** Agregada raíz que protege las invariantes propias de una reserva. */
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

    // RN-22 - El valor y la política de una reserva quedan congelados al crearla y solo cambian por modificación explícita.
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
     * RN-22 - El valor y la política de una reserva quedan congelados al crearla y solo cambian por modificación explícita.
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

    public void confirmar(java.math.BigDecimal anticipoRequerido, java.math.BigDecimal pagado) {
        exigir(anticipoRequerido != null && pagado != null, "Los valores del anticipo son obligatorios.");
        exigir(pagado.compareTo(anticipoRequerido) >= 0, "El anticipo requerido debe estar cubierto.");
        confirmar();
    }

    public void modificar(ApartamentoId apartamentoId, List<Ocupante> ocupantes, Estancia estancia,
                          LocalTime horaEstimadaLlegada, Dinero valor, LocalDateTime ahora) {
        exigir(estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA,
                "Solo se puede modificar una reserva pendiente o confirmada.");
        exigir(ahora != null && estancia != null && !estancia.fechaEntrada().isBefore(ahora.toLocalDate()),
                "RN-04 - La entrada no puede ser anterior a la fecha actual.");
        exigir(apartamentoId != null && ocupantes != null && !ocupantes.isEmpty()
                        && !ocupantes.contains(null) && horaEstimadaLlegada != null && valor != null,
                "Los datos de la reserva modificada son obligatorios.");
        this.apartamentoId = apartamentoId;
        this.ocupantes = List.copyOf(ocupantes);
        this.estancia = estancia;
        this.horaEstimadaLlegada = horaEstimadaLlegada;
        this.valorCongelado = valor;
    }

    public boolean haVencidoPlazoConfirmacion(LocalDateTime ahora, int plazoHoras) {
        exigir(ahora != null && plazoHoras > 0, "La fecha actual y el plazo deben ser válidos.");
        return estado == EstadoReserva.PENDIENTE
                && !ahora.isBefore(fechaCreacion.plusHours(plazoHoras));
    }

    public static Reserva recontruir(ReservaId id, FolioId folioId, ApartamentoId apartamentoId,
                                     Titular titular, List<Ocupante> ocupantes, Estancia estancia,
                                     String identificadorExterno, LocalTime horaEstimadaLlegada,
                                     CanalOrigen canalOrigen, EstadoReserva estado, Dinero valorCongelado,
                                     PoliticaCancelacion politicaCancelacion, LocalDateTime fechaCreacion,
                                     String motivoCancelacion) {
        Reserva reserva = new Reserva(id, folioId, apartamentoId, titular, ocupantes, estancia,
                identificadorExterno, horaEstimadaLlegada, canalOrigen, valorCongelado,
                politicaCancelacion, fechaCreacion);
        exigir(estado != null, "La reserva debe tener estado.");
        reserva.estado = estado;
        return reserva;
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

    /** Regla de ciclo de vida: solo una reserva pendiente o confirmada puede cancelarse. */
    public void cancelar() {
        exigir(estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA,
                "Solo una reserva pendiente o confirmada puede cancelarse.");
        estado = EstadoReserva.CANCELADA;
    }

    /** Regla de ciclo de vida: únicamente una reserva CONFIRMADA puede pasar al estado NO_SHOW. */
    public void marcarNoShow() {
        exigir(estado == EstadoReserva.CONFIRMADA, "Solo una reserva confirmada puede marcarse como no-show.");
        estado = EstadoReserva.NO_SHOW;
    }

    /** RN-01, RN-12: una reserva activa (PENDIENTE, CONFIRMADA o EN_CURSO) ocupa noches. */
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

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }


    /** Registra la llegada de los huéspedes, iniciando formalmente la estancia de la reserva. */
    public void registrarLlegada(LocalDate fechaActual) {
        exigir(fechaActual != null, "La fecha actual es obligatoria para registrar la llegada.");
        // RN-08 / Ciclo de vida: solo una reserva confirmada (o pendiente) puede iniciar su estancia
        exigir(estado == EstadoReserva.CONFIRMADA || estado == EstadoReserva.PENDIENTE,
                "Solo una reserva confirmada o pendiente puede registrar su llegada.");

        // Puedes aprovechar el método que ya tienes o cambiar directamente el estado
        iniciarEstancia();
    }

    public void registrarSalida(LocalDate fechaActual) {
        exigir(estado == EstadoReserva.EN_CURSO, "Solo una reserva en curso puede registrar su salida.");
        this.estado = EstadoReserva.FINALIZADA;
    }

    private static String normalizarIdentificador(String identificador) {
        if (identificador == null || identificador.isBlank()) return null;
        return identificador.trim();
    }

    public String getCodigo() {
        return id.toString();
    }

    public String getApartamento() {
        return apartamentoId.toString();
    }

}
