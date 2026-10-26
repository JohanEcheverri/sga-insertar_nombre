package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.AlojamientoId;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;
import co.uniquindio.edu.sga.domain.valueobject.Tarifa;
import co.uniquindio.edu.sga.domain.valueobject.TemporadaId;
import co.uniquindio.edu.sga.domain.valueobject.Ubicacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Agregado raíz que coordina apartamentos, temporadas, tarifas y configuración del alojamiento. */
public class Alojamiento {
    private final AlojamientoId id;
    private final String nombre;
    private final Ubicacion ubicacion;
    private final ConfiguracionAlojamiento configuracion;
    private final List<Apartamento> apartamentos;
    private final List<Temporada> temporadas;
    private PoliticaCancelacion politicaVigente;

    private Alojamiento(AlojamientoId id, String nombre, Ubicacion ubicacion,
                        ConfiguracionAlojamiento configuracion, List<Apartamento> apartamentos,
                        List<Temporada> temporadas, PoliticaCancelacion politicaVigente) {
        this.id = id;
        this.nombre = nombre.trim();
        this.ubicacion = ubicacion;
        this.configuracion = configuracion;
        this.apartamentos = new ArrayList<>(apartamentos);
        this.temporadas = new ArrayList<>(temporadas);
        this.politicaVigente = politicaVigente;
    }

    /**
     * Crea el alojamiento validando sus componentes y las invariantes entre sus apartamentos y temporadas.
     * F-05 - Existen temporadas que modifican la tarifa, incluida una temporada base obligatoria.
     * RN-27 - Las temporadas no se pueden solapar entre sí; todo periodo no cubierto se considera base.
     */
    public static Alojamiento crear(AlojamientoId id, String nombre, Ubicacion ubicacion,
                                    ConfiguracionAlojamiento configuracion,
                                    List<Apartamento> apartamentos, List<Temporada> temporadas,
                                    PoliticaCancelacion politicaVigente) {
        exigir(id != null, "El alojamiento debe tener identificador.");
        exigir(nombre != null && !nombre.isBlank(), "El alojamiento debe tener nombre.");
        exigir(ubicacion != null, "El alojamiento debe tener ubicación.");
        exigir(configuracion != null, "El alojamiento debe tener configuración.");
        exigir(apartamentos != null && !apartamentos.isEmpty(), "El alojamiento debe tener al menos un apartamento.");
        exigir(!apartamentos.contains(null), "La lista de apartamentos no puede contener valores nulos.");
        exigir(temporadas != null && !temporadas.isEmpty(), "El alojamiento debe tener temporadas configuradas.");
        exigir(!temporadas.contains(null), "La lista de temporadas no puede contener valores nulos.");
        exigir(politicaVigente != null, "El alojamiento debe tener una política de cancelación vigente.");

        validarApartamentosUnicos(apartamentos);
        validarTemporadas(temporadas, apartamentos);
        return new Alojamiento(id, nombre, ubicacion, configuracion,
                apartamentos, temporadas, politicaVigente);
    }

    /** Agrega un apartamento únicamente si todas las temporadas ya tienen su tarifa correspondiente. */
    public void agregarApartamento(Apartamento apartamento) {
        exigir(apartamento != null, "El apartamento no puede ser nulo.");
        exigir(apartamentos.stream().noneMatch(actual -> actual.getId().equals(apartamento.getId())),
                "El alojamiento ya contiene un apartamento con ese identificador.");
        exigir(apartamentos.stream().noneMatch(actual -> actual.getIdentificadorUnico()
                        .equalsIgnoreCase(apartamento.getIdentificadorUnico())),
                "El identificador único del apartamento ya existe en este alojamiento.");
        exigir(temporadas.stream().allMatch(temporada -> tieneTarifa(temporada, apartamento.getId())),
                "Debe registrarse una tarifa del apartamento en cada temporada antes de agregarlo.");
        apartamentos.add(apartamento);
    }

    /**
     * Agrega una temporada al alojamiento.
     * RN-27 - Las temporadas no se pueden solapar entre sí; todo periodo no cubierto se considera base.
     */
    public void agregarTemporada(Temporada temporada) {
        exigir(temporada != null, "La temporada no puede ser nula.");
        exigir(temporadas.stream().noneMatch(actual -> actual.getId().equals(temporada.getId())),
                "Ya existe una temporada con ese identificador.");
        if (temporada.esBase()) {
            exigir(temporadas.stream().noneMatch(Temporada::esBase),
                    "El alojamiento solo puede tener una temporada base.");
        }
        for (Temporada existente : temporadas) {
            exigir(!temporada.seSolapaCon(existente), "RN-27 - Las temporadas no se pueden solapar entre sí.");
        }
        validarTarifasParaApartamentos(temporada, apartamentos);
        temporadas.add(temporada);
    }

    /** No se permite retirar la temporada base obligatoria del alojamiento. */
    public void retirarTemporada(TemporadaId temporadaId) {
        exigir(temporadaId != null, "El identificador de temporada no puede ser nulo.");
        Temporada temporada = temporadas.stream()
                .filter(actual -> actual.getId().equals(temporadaId))
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException("La temporada no pertenece a este alojamiento."));
        exigir(!temporada.esBase(), "F-05 - La temporada base es obligatoria y no se puede retirar.");
        temporadas.remove(temporada);
    }

    /** RN-27 - Usa la temporada especial que cubre la fecha; cuando ninguna la cubre, devuelve la base. */
    public Temporada temporadaAplicable(LocalDate fecha) {
        exigir(fecha != null, "La fecha consultada no puede ser nula.");
        Temporada temporadaBase = temporadas.stream()
                .filter(Temporada::esBase)
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException("F-05 - El alojamiento debe tener temporada base."));
        List<Temporada> aplicables = temporadas.stream()
                .filter(temporada -> temporada.cubreFecha(fecha))
                .toList();
        exigir(aplicables.size() <= 1, "RN-27 - Las temporadas no se pueden solapar entre sí.");
        return aplicables.isEmpty() ? temporadaBase : aplicables.get(0);
    }

    /** Devuelve la tarifa del apartamento para la fecha, resolviendo la temporada aplicable. */
    public co.uniquindio.edu.sga.domain.valueobject.Tarifa tarifaAplicable(ApartamentoId apartamentoId,
                                                                           LocalDate fecha) {
        exigir(apartamentos.stream().anyMatch(apartamento -> apartamento.getId().equals(apartamentoId)),
                "El apartamento no pertenece a este alojamiento.");
        return temporadaAplicable(fecha).tarifaPara(apartamentoId);
    }

    public void cambiarPoliticaVigente(PoliticaCancelacion nuevaPolitica) {
        exigir(nuevaPolitica != null, "La política de cancelación vigente no puede ser nula.");
        politicaVigente = nuevaPolitica;
    }

    /** Mantiene la actualización de tarifas dentro del agregado y evita tarifas para apartamentos externos. */
    public void establecerTarifa(Tarifa tarifa) {
        exigir(tarifa != null, "La tarifa no puede ser nula.");
        exigir(apartamentos.stream().anyMatch(apartamento -> apartamento.getId().equals(tarifa.apartamentoId())),
                "La tarifa debe corresponder a un apartamento de este alojamiento.");
        Temporada temporada = temporadas.stream()
                .filter(actual -> actual.getId().equals(tarifa.temporadaId()))
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException("La tarifa debe corresponder a una temporada de este alojamiento."));
        temporada.establecerTarifa(tarifa);
    }

    private static void validarApartamentosUnicos(List<Apartamento> apartamentos) {
        long idsDistintos = apartamentos.stream().map(Apartamento::getId).distinct().count();
        exigir(idsDistintos == apartamentos.size(), "No puede haber apartamentos con identificadores repetidos.");
        long identificadoresDistintos = apartamentos.stream()
                .map(apartamento -> apartamento.getIdentificadorUnico().toLowerCase(java.util.Locale.ROOT))
                .distinct().count();
        exigir(identificadoresDistintos == apartamentos.size(), "Los identificadores únicos de apartamentos no se pueden repetir.");
    }

    private static void validarTemporadas(List<Temporada> temporadas, List<Apartamento> apartamentos) {
        long bases = temporadas.stream().filter(Temporada::esBase).count();
        exigir(bases == 1, "F-05 - El alojamiento debe tener exactamente una temporada base.");

        long idsDistintos = temporadas.stream().map(Temporada::getId).distinct().count();
        exigir(idsDistintos == temporadas.size(), "No puede haber temporadas con identificadores repetidos.");

        for (int i = 0; i < temporadas.size(); i++) {
            Temporada temporada = temporadas.get(i);
            validarTarifasParaApartamentos(temporada, apartamentos);
            for (int j = i + 1; j < temporadas.size(); j++) {
                exigir(!temporada.seSolapaCon(temporadas.get(j)),
                        "RN-27 - Las temporadas no se pueden solapar entre sí.");
            }
        }
    }

    private static void validarTarifasParaApartamentos(Temporada temporada,
                                                        List<Apartamento> apartamentos) {
        for (Apartamento apartamento : apartamentos) {
            exigir(tieneTarifa(temporada, apartamento.getId()),
                    "Cada temporada debe tener una tarifa para cada apartamento del alojamiento.");
        }
        exigir(temporada.getTarifas().stream().allMatch(tarifa -> apartamentos.stream()
                        .anyMatch(apartamento -> apartamento.getId().equals(tarifa.apartamentoId()))),
                "La temporada no puede tener tarifas para apartamentos ajenos al alojamiento.");
    }

    private static boolean tieneTarifa(Temporada temporada, ApartamentoId apartamentoId) {
        return temporada.getTarifas().stream()
                .anyMatch(tarifa -> tarifa.apartamentoId().equals(apartamentoId));
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }

    public AlojamientoId getId() { return id; }
    public String getNombre() { return nombre; }
    public Ubicacion getUbicacion() { return ubicacion; }
    public ConfiguracionAlojamiento getConfiguracion() { return configuracion; }
    public List<Apartamento> getApartamentos() { return List.copyOf(apartamentos); }
    public List<Temporada> getTemporadas() { return List.copyOf(temporadas); }
    public PoliticaCancelacion getPoliticaVigente() { return politicaVigente; }
}
