package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Datos modificables de un apartamento existente.
 * @param descripcion Nueva descripción del apartamento.
 * @param aforoMaximo Capacidad máxima actualizada.
 * @param disponible Indica si el apartamento debe figurar disponible.
 * @author Equipo SGA
 * @version 1.0
 */
public record ModificarApartamentoRequest(

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotNull(message = "El aforo máximo es obligatorio") @Positive(message = "El aforo debe ser mayor que cero")
        Integer aforoMaximo,

        @NotNull(message = "Debe indicar si el apartamento está disponible")
        Boolean disponible
) { }
