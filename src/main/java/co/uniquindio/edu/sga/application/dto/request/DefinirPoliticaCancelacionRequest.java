package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.math.BigDecimal;

/** Solicitud para definir la política de cancelación inicial del alojamiento.
 * @param version Identificador de versión de la política.
 * @param tramos Retenciones aplicables según los días de antelación.
 * @param porcentajeRetencionNoShow Porcentaje retenido cuando ocurre un no-show.
 * @author Equipo SGA
 * @version 1.0
 */
public record DefinirPoliticaCancelacionRequest(

        @NotBlank(message = "La versión de la política es obligatoria")
        String version,

        @NotEmpty(message = "La política debe incluir al menos un tramo")
        List<@Valid TramoCancelacionRequest> tramos,

        @NotNull(message = "La retención por no-show es obligatoria") @DecimalMin(value = "0.0", message = "La retención mínima es 0") @DecimalMax(value = "1.0", message = "La retención máxima es 1")
        BigDecimal porcentajeRetencionNoShow
) { }
