package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.BloqueoId;
import co.uniquindio.edu.sga.domain.valueobject.Estancia;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;

/** Entidad que reserva un intervalo del calendario del apartamento para mantenimiento u otro motivo. */
public class Bloqueo {
    private final BloqueoId id;
    private final ApartamentoId apartamentoId;
    private final RangoFechas rangoFechas;
    private final String motivo;
    private boolean activo;

    private Bloqueo(BloqueoId id, ApartamentoId apartamentoId, RangoFechas rangoFechas,
                    String motivo, boolean activo) {
        this.id = id;
        this.apartamentoId = apartamentoId;
        this.rangoFechas = rangoFechas;
        this.motivo = motivo.trim();
        this.activo = activo;
    }

    /** Regla de creación: todo bloqueo identifica apartamento, periodo y motivo. */
    public static Bloqueo crear(BloqueoId id, ApartamentoId apartamentoId,
                                RangoFechas rangoFechas, String motivo) {
        exigir(id != null, "El bloqueo debe tener identificador.");
        exigir(apartamentoId != null, "El bloqueo debe estar asociado a un apartamento.");
        exigir(rangoFechas != null, "El bloqueo debe tener un rango de fechas.");
        exigir(motivo != null && !motivo.isBlank(), "El motivo del bloqueo es obligatorio.");
        return new Bloqueo(id, apartamentoId, rangoFechas, motivo, true);
    }

    /** Atajo que genera automáticamente el identificador del bloqueo. */
    public static Bloqueo crear(ApartamentoId apartamentoId, RangoFechas rangoFechas, String motivo) {
        return crear(BloqueoId.generar(), apartamentoId, rangoFechas, motivo);
    }

    /**
     * RN-07 - Un apartamento con bloqueo vigente sobre una noche no está disponible para esa noche.
     * Determina si este bloqueo activo cubre por completo el rango consultado.
     */
    public boolean bloqueaTodoElRango(RangoFechas rango) {
        exigir(rango != null, "El rango consultado no puede ser nulo.");
        return activo && rangoFechas.cubre(rango);
    }

    /**
     * RN-07 - Un apartamento con bloqueo vigente sobre una noche no está disponible para esa noche.
     * Determina si este bloqueo activo se cruza con alguna noche de la estancia.
     */
    public boolean seCruzaConEstancia(Estancia estancia) {
        exigir(estancia != null, "La estancia consultada no puede ser nula.");
        return activo && rangoFechas.intersecta(estancia.rangoFechas());
    }

    public void desactivar() {
        exigir(activo, "El bloqueo ya está inactivo.");
        activo = false;
    }

    public void reactivar() {
        exigir(!activo, "El bloqueo ya está activo.");
        activo = true;
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }

    public BloqueoId getId() { return id; }
    public ApartamentoId getApartamentoId() { return apartamentoId; }
    public RangoFechas getRangoFechas() { return rangoFechas; }
    public String getMotivo() { return motivo; }
    public boolean isActivo() { return activo; }
}
