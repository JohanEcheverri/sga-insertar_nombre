package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;
import co.uniquindio.edu.sga.domain.valueobject.Tarifa;
import co.uniquindio.edu.sga.domain.valueobject.TemporadaId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

/** Periodo tarifario del alojamiento; la temporada base no está limitada a fechas. */
public class Temporada {
    private final TemporadaId id;
    private final String nombre;
    private final RangoFechas rangoFechas;
    private final boolean esBase;
    private final Map<ApartamentoId, Tarifa> tarifasPorApartamento;

    private Temporada(TemporadaId id, String nombre, RangoFechas rangoFechas,
                      boolean esBase, List<Tarifa> tarifas) {
        this.id = id;
        this.nombre = nombre.trim();
        this.rangoFechas = rangoFechas;
        this.esBase = esBase;
        this.tarifasPorApartamento = new LinkedHashMap<>();
        for (Tarifa tarifa : tarifas) {
            this.tarifasPorApartamento.put(tarifa.apartamentoId(), tarifa);
        }
    }

    /** Crea una temporada con fechas concretas y tarifas para apartamentos. */
    public static Temporada crear(TemporadaId id, String nombre, RangoFechas rangoFechas,
                                  List<Tarifa> tarifas) {
        validarDatosComunes(id, nombre, tarifas);
        exigir(rangoFechas != null, "Una temporada distinta de la base debe tener un rango de fechas.");
        validarTarifasDeTemporada(id, tarifas);
        validarTarifasUnicas(tarifas);
        return new Temporada(id, nombre, rangoFechas, false, tarifas);
    }

    /** Crea la temporada de respaldo para los periodos sin una temporada especial aplicable. */
    public static Temporada crearBase(TemporadaId id, List<Tarifa> tarifas) {
        validarDatosComunes(id, "Base", tarifas);
        validarTarifasDeTemporada(id, tarifas);
        validarTarifasUnicas(tarifas);
        return new Temporada(id, "Base", null, true, tarifas);
    }

    /** Registra o reemplaza la tarifa de un apartamento en esta temporada. */
    public void establecerTarifa(Tarifa tarifa) {
        exigir(tarifa != null, "La tarifa no puede ser nula.");
        exigir(id.equals(tarifa.temporadaId()), "La tarifa debe pertenecer a esta temporada.");
        tarifasPorApartamento.put(tarifa.apartamentoId(), tarifa);
    }

    /** Obtiene la tarifa del apartamento; elegir la temporada según fechas corresponde al servicio de cotización. */
    public Tarifa tarifaPara(ApartamentoId apartamentoId) {
        exigir(apartamentoId != null, "El apartamento consultado no puede ser nulo.");
        Tarifa tarifa = tarifasPorApartamento.get(apartamentoId);
        if (tarifa == null) {
            throw new ReglaDominioException("No existe tarifa para el apartamento indicado en esta temporada.");
        }
        return tarifa;
    }

    /** RN-27 - Una temporada especial aplica únicamente a las fechas de su rango; la base es el respaldo. */
    public boolean cubreFecha(LocalDate fecha) {
        exigir(fecha != null, "La fecha consultada no puede ser nula.");
        return !esBase && !fecha.isBefore(rangoFechas.fechaInicio())
                && fecha.isBefore(rangoFechas.fechaFin());
    }

    /** RN-27 - Dos temporadas con fechas definidas no pueden solaparse. La temporada base no tiene rango. */
    public boolean seSolapaCon(Temporada otra) {
        exigir(otra != null, "La temporada comparada no puede ser nula.");
        return !esBase && !otra.esBase && rangoFechas.intersecta(otra.rangoFechas);
    }

    private static void validarDatosComunes(TemporadaId id, String nombre, List<Tarifa> tarifas) {
        exigir(id != null, "La temporada debe tener identificador.");
        exigir(nombre != null && !nombre.isBlank(), "La temporada debe tener nombre.");
        exigir(tarifas != null && !tarifas.isEmpty(), "La temporada debe definir tarifas.");
        exigir(!tarifas.contains(null), "La lista de tarifas no puede contener valores nulos.");
    }

    private static void validarTarifasDeTemporada(TemporadaId id, List<Tarifa> tarifas) {
        for (Tarifa tarifa : tarifas) {
            exigir(id.equals(tarifa.temporadaId()), "Todas las tarifas deben pertenecer a la temporada creada.");
        }
    }

    private static void validarTarifasUnicas(List<Tarifa> tarifas) {
        long cantidadApartamentos = tarifas.stream().map(Tarifa::apartamentoId).distinct().count();
        exigir(cantidadApartamentos == tarifas.size(), "No puede haber más de una tarifa por apartamento en la temporada.");
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }

    public TemporadaId getId() { return id; }
    public String getNombre() { return nombre; }
    public RangoFechas getRangoFechas() { return rangoFechas; }
    public boolean esBase() { return esBase; }
    public List<Tarifa> getTarifas() { return List.copyOf(tarifasPorApartamento.values()); }
}
