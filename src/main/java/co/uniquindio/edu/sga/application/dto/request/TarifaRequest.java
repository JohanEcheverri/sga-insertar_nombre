package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Tarifa inicial de un apartamento para una temporada.
 * @param temporadaId Identificador de la temporada tarifaria.
 * @param precioPorNochePorOcupante Precio cobrado por noche y ocupante facturable.
 * @author Equipo SGA
 * @version 1.0
 */
public record TarifaRequest(
        @NotBlank(message = "El identificador de temporada es obligatorio") String temporadaId,
        @NotNull(message = "El precio de la tarifa es obligatorio") @Positive(message = "El precio debe ser positivo") BigDecimal precioPorNochePorOcupante
) { }
