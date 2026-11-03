package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.math.BigDecimal;

/** Solicitud para reemplazar la política vigente por una versión nueva.
 * @param version Identificador de la nueva versión de la política.
 * @param tramos Umbrales de antelación y porcentajes de retención que tendrá la política.
 * @param porcentajeRetencionNoShow Porcentaje retenido cuando el huésped no se presenta.
 * @author Equipo SGA
 * @version 1.0
 */
public record ActualizarPoliticaCancelacionRequest(

        @NotBlank(message = "La nueva versión de la política es obligatoria")
        String version,

        @NotEmpty(message = "La política debe incluir al menos un tramo")
        List<@Valid TramoCancelacionRequest> tramos,

        @NotNull(message = "La retención por no-show es obligatoria") @DecimalMin(value = "0.0", message = "La retención mínima es 0") @DecimalMax(value = "1.0", message = "La retención máxima es 1")
        BigDecimal porcentajeRetencionNoShow
) { }
