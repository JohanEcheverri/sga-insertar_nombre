package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.FolioSaldoPendienteException;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.AutorizacionCierre;
import co.uniquindio.edu.sga.domain.valueobject.Cargo;
import co.uniquindio.edu.sga.domain.valueobject.EstadoFolio;
import co.uniquindio.edu.sga.domain.valueobject.FolioId;
import co.uniquindio.edu.sga.domain.valueobject.Pago;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Agregado raíz responsable del estado contable de una reserva. */
public class Folio {
    private final FolioId id;
    private final ReservaId reservaId;
    private EstadoFolio estado;
    private final List<Cargo> cargos;
    private final List<Pago> pagos;
    private AutorizacionCierre autorizacionCierre;

    private Folio(FolioId id, ReservaId reservaId) {
        this.id = id;
        this.reservaId = reservaId;
        this.estado = EstadoFolio.ABIERTO;
        this.cargos = new ArrayList<>();
        this.pagos = new ArrayList<>();
    }

    public static Folio crear(FolioId id, ReservaId reservaId) {
        exigir(id != null, "El folio debe tener identificador.");
        exigir(reservaId != null, "El folio debe estar asociado a una reserva.");
        return new Folio(id, reservaId);
    }

    /** Solo un folio ABIERTO puede recibir cargos. */
    public void registrarCargo(Cargo cargo) {
        exigirAbierto();
        exigir(cargo != null, "El cargo no puede ser nulo.");
        cargos.add(cargo);
    }

    /** RN-15 - Solo un folio ABIERTO puede recibir pagos. */
    public void registrarPago(Pago pago) {
        exigirAbierto();
        exigir(pago != null, "El pago no puede ser nulo.");
        pagos.add(pago);
    }

    /** Cierre ordinario: exige saldo pendiente en cero. Conserva quién cerró, cuándo y por qué. */
    public void cerrar(AutorizacionCierre autorizacion) {
        exigirAbierto();
        exigir(autorizacion != null, "El cierre del folio requiere autorización.");
        if (tieneSaldoPendiente()) {
            throw new FolioSaldoPendienteException(id.valor().toString());
        }
        this.autorizacionCierre = autorizacion;
        this.estado = EstadoFolio.CERRADO;
    }

    /** RN-17 - Cierre con saldo pendiente: solo con autorización explícita registrada (la da el administrador). */
    public void cerrarConSaldoAutorizado(AutorizacionCierre autorizacion) {
        exigirAbierto();
        exigir(autorizacion != null, "El cierre con saldo pendiente requiere autorización explícita.");
        this.autorizacionCierre = autorizacion;
        this.estado = EstadoFolio.CERRADO;
    }

    public void reabrir(AutorizacionCierre autorizacion) {
        exigir(estado == EstadoFolio.CERRADO, "Solo un folio cerrado puede reabrirse.");
        exigir(autorizacion != null, "La reapertura del folio requiere autorización.");
        this.autorizacionCierre = autorizacion;
        this.estado = EstadoFolio.ABIERTO;
    }

    /** Suma neta de cargos: los cargos de tipo crédito (reversos, ajustes a favor) restan. */
    public BigDecimal totalCargos() {
        return cargos.stream()
                .map(c -> {
                    BigDecimal monto = BigDecimal.valueOf(c.monto().valor());
                    return c.tipoCargo().esCredito() ? monto.negate() : monto;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalPagos() {
        return pagos.stream().map(p -> BigDecimal.valueOf(p.monto().valor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** RN-15 - Saldo con signo: positivo debe el huésped, cero saldado, negativo saldo a favor. */
    public BigDecimal saldo() {
        return totalCargos().subtract(totalPagos());
    }

    /** El saldo pendiente se limita a deuda; un saldo a favor se considera saldado. */
    public BigDecimal saldoPendiente() {
        return saldo().max(BigDecimal.ZERO);
    }

    /** Monto a devolver al huésped (el sistema lo registra; el desembolso ocurre fuera, 3.4). */
    public BigDecimal saldoAFavor() {
        return saldo().negate().max(BigDecimal.ZERO);
    }

    public boolean tieneSaldoPendiente() {
        return saldoPendiente().compareTo(BigDecimal.ZERO) > 0;
    }

    private void exigirAbierto() {
        exigir(estado == EstadoFolio.ABIERTO, "La operación requiere que el folio esté ABIERTO.");
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }

    public FolioId getId() { return id; }
    public ReservaId getReservaId() { return reservaId; }
    public EstadoFolio getEstado() { return estado; }
    public List<Cargo> getCargos() { return List.copyOf(cargos); }
    public List<Pago> getPagos() { return List.copyOf(pagos); }
    public AutorizacionCierre getAutorizacionCierre() { return autorizacionCierre; }
}
