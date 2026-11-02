package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;


/** Solicitud para asignar a un apartamento una tarifa por noche dentro de una temporada.
 * @param temporadaId Identificador de la temporada a la que pertenece la tarifa.
 * @param apartamentoId Identificador del apartamento tarifado.
 * @param precioPorNochePorOcupante Precio aplicado por noche y por cada ocupante facturable.
 * @author Equipo SGA
 * @version 1.0
 */
public record AsignarTarifaRequest(

        @NotBlank(message = "El identificador de temporada es obligatorio")
        String temporadaId,

        @NotBlank(message = "El identificador de apartamento es obligatorio")
        String apartamentoId,

        @NotNull(message = "El precio por noche y ocupante es obligatorio") @Positive(message = "El precio debe ser positivo")
        BigDecimal precioPorNochePorOcupante
) { }
