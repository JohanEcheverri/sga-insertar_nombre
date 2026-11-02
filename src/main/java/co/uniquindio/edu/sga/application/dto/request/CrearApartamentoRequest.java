package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/** Datos de entrada para crear un apartamento junto con sus dormitorios.
 * @param identificadorUnico Código legible único del apartamento dentro del alojamiento.
 * @param descripcion Descripción general del apartamento.
 * @param aforoMaximo Cantidad máxima de ocupantes permitida.
 * @param dormitorios Dormitorios que se registran como parte del apartamento.
 * @param tarifas Tarifas iniciales por cada temporada del alojamiento.
 * @author Equipo SGA
 * @version 1.0
 */
public record CrearApartamentoRequest(

        @NotBlank(message = "El identificador único es obligatorio")
        String identificadorUnico,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotNull(message = "El aforo máximo es obligatorio") @Positive(message = "El aforo debe ser mayor que cero")
        Integer aforoMaximo,

        @NotEmpty(message = "Debe registrar al menos un dormitorio") List<@Valid DormitorioRequest> dormitorios,

        @NotEmpty(message = "Debe proporcionar una tarifa por cada temporada") List<@Valid TarifaRequest> tarifas
) { }
