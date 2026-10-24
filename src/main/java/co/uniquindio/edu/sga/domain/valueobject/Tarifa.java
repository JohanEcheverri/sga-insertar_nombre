package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.math.BigDecimal;

/** Precio por noche y ocupante para un apartamento específico durante una temporada. */
public record Tarifa(TemporadaId temporadaId, ApartamentoId apartamentoId,
                     Dinero precioPorNochePorOcupante) {
    public Tarifa {
        // Regla de tarifa: cada precio debe pertenecer a una temporada y un apartamento, y tener un monto válido.
        if (temporadaId == null) {
            throw new ReglaDominioException("La tarifa debe estar asociada a una temporada.");
        }
        if (apartamentoId == null) {
            throw new ReglaDominioException("La tarifa debe estar asociada a un apartamento.");
        }
        if (precioPorNochePorOcupante == null) {
            throw new ReglaDominioException("El precio por noche y ocupante es obligatorio.");
        }
    }

    /**
     * RN-05 - El valor de la estancia es la suma, noche por noche, de la tarifa vigente del
     * apartamento en la temporada de esa noche, multiplicada por el número de ocupantes facturables.
     * Este método calcula solo una noche; el servicio de cotización suma los importes de toda la estancia
     * seleccionando para cada noche la temporada aplicable.
     */
    public BigDecimal calcularValorNoche(int ocupantesFacturables) {
        if (ocupantesFacturables <= 0) {
            throw new ReglaDominioException("El número de ocupantes facturables debe ser mayor a cero.");
        }
        return BigDecimal.valueOf(precioPorNochePorOcupante.valor())
                .multiply(BigDecimal.valueOf(ocupantesFacturables));
    }
}
