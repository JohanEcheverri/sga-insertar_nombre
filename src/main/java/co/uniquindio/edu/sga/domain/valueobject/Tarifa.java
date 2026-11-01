package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.math.BigDecimal;

/** Precio por noche y ocupante para un apartamento específico durante una temporada. */
public record Tarifa(TemporadaId temporadaId, ApartamentoId apartamentoId,
                     Dinero precioPorNochePorOcupante) {
    public Tarifa {
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

    /** Método auxiliar para que el apartamento pueda filtrar por el identificador de la temporada. */
    public Object getTemporadaId() {
        return temporadaId;
    }

    public BigDecimal calcularValorNoche(int ocupantesFacturables) {
        if (ocupantesFacturables <= 0) {
            throw new ReglaDominioException("El número de ocupantes facturables debe ser mayor a cero.");
        }
        return precioPorNochePorOcupante.valor()
                .multiply(BigDecimal.valueOf(ocupantesFacturables));
    }
}