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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

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

    public static Alojamiento crear(AlojamientoId id, String nombre, Ubicacion ubicacion,
                                    ConfiguracionAlojamiento configuracion,
                                    List<Apartamento> apartamentos, List<Temporada> temporadas,
                                    PoliticaCancelacion politicaVigente) {
        exigir(id != null, "El alojamiento debe tener identificador.");
        exigir(nombre != null && !nombre.isBlank(), "El alojamiento debe tener nombre.");
        exigir(ubicacion != null, "El alojamiento debe tener ubicación.");
        exigir(configuracion != null, "El alojamiento debe tener configuración.");
        exigir(apartamentos != null && !apartamentos.isEmpty(), "El alojamiento debe tener al menos un apartamento.");
        exigir(apartamentos.stream().noneMatch(Objects::isNull), "La lista de apartamentos no puede contener valores nulos.");
        exigir(temporadas != null && !temporadas.isEmpty(), "El alojamiento debe tener temporadas configuradas.");
        exigir(temporadas.stream().noneMatch(Objects::isNull), "La lista de temporadas no puede contener valores nulos.");
        exigir(politicaVigente != null, "El alojamiento debe tener una política de cancelación vigente.");

        validarApartamentosUnicos(apartamentos);
        validarTemporadas(temporadas, apartamentos);
        return new Alojamiento(id, nombre, ubicacion, configuracion,
                apartamentos, temporadas, politicaVigente);
    }

    /** Agrega un apartamento únicamente si todas las temporadas ya tienen su tarifa correspondiente. */
    public void agregarApartamento(Apartamento apartamento) {
        validarNuevoApartamento(apartamento);
        exigir(temporadas.stream().allMatch(temporada -> tieneTarifa(temporada, apartamento.getId())),
                "Debe registrarse una tarifa del apartamento en cada temporada antes de agregarlo.");
        apartamentos.add(apartamento);
    }

    /**
     * Agrega un apartamento junto con sus tarifas iniciales (una por temporada). Resuelve el círculo
     * "no hay tarifa sin apartamento / no hay apartamento sin tarifa": valida todo y recién entonces aplica.
     */
    public void agregarApartamento(Apartamento apartamento, List<Tarifa> tarifasIniciales) {
        validarNuevoApartamento(apartamento);
        exigir(tarifasIniciales != null && tarifasIniciales.stream().noneMatch(Objects::isNull),
                "Las tarifas iniciales del apartamento son obligatorias.");
        for (Tarifa tarifa : tarifasIniciales) {
            exigir(tarifa.apartamentoId().equals(apartamento.getId()),
                    "Las tarifas iniciales deben pertenecer al apartamento que se agrega.");
        }
        for (Temporada temporada : temporadas) {
            long cantidad = tarifasIniciales.stream()
                    .filter(t -> t.temporadaId().equals(temporada.getId())).count();
            exigir(cantidad == 1,
                    "Debe registrarse exactamente una tarifa del apartamento en cada temporada (falta/sobra en: "
                            + temporada.getNombre() + ").");
        }
        exigir(tarifasIniciales.stream().allMatch(t -> temporadas.stream()
                        .anyMatch(temporada -> temporada.getId().equals(t.temporadaId()))),
                "Hay tarifas para temporadas que no pertenecen a este alojamiento.");
        for (Tarifa tarifa : tarifasIniciales) {
            temporadas.stream().filter(t -> t.getId().equals(tarifa.temporadaId())).findFirst()
                    .orElseThrow().establecerTarifa(tarifa);
        }
        apartamentos.add(apartamento);
    }

    /** RN-27 - Las temporadas no se pueden solapar entre sí; todo periodo no cubierto se considera base. */
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

    /**
     * Reemplaza una temporada existente (mismo id) por una nueva versión — p. ej. al cambiar sus
     * fechas o nombre. Atómico: si la nueva versión viola alguna regla, el agregado queda intacto.
     */
    public void reemplazarTemporada(Temporada nueva) {
        exigir(nueva != null, "La temporada no puede ser nula.");
        int indice = -1;
        for (int i = 0; i < temporadas.size(); i++) {
            if (temporadas.get(i).getId().equals(nueva.getId())) indice = i;
        }
        exigir(indice >= 0, "La temporada no pertenece a este alojamiento.");
        exigir(temporadas.get(indice).esBase() == nueva.esBase(),
                "F-05 - No se puede convertir la temporada base en especial ni al revés.");
        for (int i = 0; i < temporadas.size(); i++) {
            if (i != indice) {
                exigir(!nueva.seSolapaCon(temporadas.get(i)), "RN-27 - Las temporadas no se pueden solapar entre sí.");
            }
        }
        validarTarifasParaApartamentos(nueva, apartamentos);
        temporadas.set(indice, nueva);
    }

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

    public Tarifa tarifaAplicable(ApartamentoId apartamentoId, LocalDate fecha) {
        exigir(apartamentos.stream().anyMatch(apartamento -> apartamento.getId().equals(apartamentoId)),
                "El apartamento no pertenece a este alojamiento.");
        return temporadaAplicable(fecha).tarifaPara(apartamentoId);
    }

    public void cambiarPoliticaVigente(PoliticaCancelacion nuevaPolitica) {
        exigir(nuevaPolitica != null, "La política de cancelación vigente no puede ser nula.");
        politicaVigente = nuevaPolitica;
    }

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

    // ---- consultas de apoyo para los casos de uso ----

    public Optional<Apartamento> buscarApartamento(ApartamentoId apartamentoId) {
        return apartamentos.stream().filter(a -> a.getId().equals(apartamentoId)).findFirst();
    }

    public Optional<Temporada> buscarTemporada(TemporadaId temporadaId) {
        return temporadas.stream().filter(t -> t.getId().equals(temporadaId)).findFirst();
    }

    /** Apartamentos vendibles: excluye los retirados (eliminación lógica). */
    public List<Apartamento> apartamentosActivos() {
        return apartamentos.stream().filter(Apartamento::isActivo).toList();
    }

    private void validarNuevoApartamento(Apartamento apartamento) {
        exigir(apartamento != null, "El apartamento no puede ser nulo.");
        exigir(apartamentos.stream().noneMatch(actual -> actual.getId().equals(apartamento.getId())),
                "El alojamiento ya contiene un apartamento con ese identificador.");
        exigir(apartamentos.stream().noneMatch(actual -> actual.getIdentificadorUnico()
                        .equalsIgnoreCase(apartamento.getIdentificadorUnico())),
                "El identificador único del apartamento ya existe en este alojamiento.");
    }

    private static void validarApartamentosUnicos(List<Apartamento> apartamentos) {
        long idsDistintos = apartamentos.stream().map(Apartamento::getId).distinct().count();
        exigir(idsDistintos == apartamentos.size(), "No puede haber apartamentos con identificadores repetidos.");
        long identificadoresDistintos = apartamentos.stream()
                .map(apartamento -> apartamento.getIdentificadorUnico().toLowerCase(Locale.ROOT))
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
