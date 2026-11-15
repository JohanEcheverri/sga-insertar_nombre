package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.Estancia;
import co.uniquindio.edu.sga.domain.valueobject.Ocupante;
import co.uniquindio.edu.sga.domain.valueobject.Titular;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Valida la composición del grupo de una reserva. La capacidad física (RN-02) la verifica
 * DisponibilidadApartamentoService junto con el resto de condiciones de disponibilidad.
 */
public class ValidacionAforoOcupantesService {

    /** Mayoría de edad en Colombia (RN-23). */
    public static final int MAYORIA_EDAD = 18;

    /**
     * RN-23 / 3.2 - El titular debe estar entre los ocupantes, ser mayor de edad y, por tanto, facturable.
     * Además no puede haber dos ocupantes con el mismo documento.
     */
    public void validarGrupo(Titular titular, List<Ocupante> ocupantes, Estancia estancia, int umbralEdadFacturable) {
        if (titular == null || ocupantes == null || ocupantes.isEmpty() || estancia == null) {
            throw new ReglaDominioException("La reserva debe tener titular, estancia y al menos un ocupante.");
        }
        if (ocupantes.stream().anyMatch(Objects::isNull)) {
            throw new ReglaDominioException("La lista de ocupantes no puede contener valores nulos.");
        }
        Set<String> documentos = new HashSet<>();
        for (Ocupante o : ocupantes) {
            if (!documentos.add(o.documentoIdentidad().numero())) {
                throw new ReglaDominioException("El documento " + o.documentoIdentidad().numero()
                        + " está repetido entre los ocupantes.");
            }
        }
        Ocupante ocupanteTitular = ocupantes.stream()
                .filter(o -> o.documentoIdentidad().equals(titular.documentoIdentidad()))
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException(
                        "RN-23 - El titular debe estar incluido entre los ocupantes de la reserva."));
        int edad = ocupanteTitular.edadEn(estancia.fechaEntrada());
        if (edad < MAYORIA_EDAD) {
            throw new ReglaDominioException("RN-23 - El titular debe ser mayor de edad.");
        }
        if (!ocupanteTitular.esFacturable(estancia.fechaEntrada(), umbralEdadFacturable)) {
            throw new ReglaDominioException("RN-23 - El titular debe ser un ocupante facturable.");
        }
    }
}
