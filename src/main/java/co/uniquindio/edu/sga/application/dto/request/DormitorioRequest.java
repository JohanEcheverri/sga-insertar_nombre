package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Datos de un dormitorio enviados dentro de la creación de un apartamento.
 * @param nombre Nombre o etiqueta del dormitorio.
 * @param cantidadCamas Número de camas disponibles en el dormitorio.
 * @param tipoCamas Descripción del tipo de camas instaladas.
 * @author Equipo SGA
 * @version 1.0
 */
public record DormitorioRequest(

        @NotBlank(message = "El nombre del dormitorio es obligatorio")
        String nombre,

        @NotNull(message = "La cantidad de camas es obligatoria") @Positive(message = "Debe haber al menos una cama")
        Integer cantidadCamas,

        @NotBlank(message = "El tipo de camas es obligatorio")
        String tipoCamas
) { }
