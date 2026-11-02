package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Datos modificables de un apartamento existente.
 * @param descripcion Nueva descripción del apartamento.
 * @param aforoMaximo Capacidad máxima actualizada.
 * @param dormitorios Configuración actualizada de los dormitorios.
 * @author Equipo SGA
 * @version 1.0
 */
public record ModificarApartamentoRequest(

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotNull(message = "El aforo máximo es obligatorio") @Positive(message = "El aforo debe ser mayor que cero")
        Integer aforoMaximo,

        @NotEmpty(message = "Debe registrar al menos un dormitorio") List<@Valid DormitorioRequest> dormitorios
) { }
