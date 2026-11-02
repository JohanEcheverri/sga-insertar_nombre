package co.uniquindio.edu.sga.application.dto.response;

import java.time.LocalDate;
/** Vista de salida de una temporada tarifaria.
 * @param id Identificador de la temporada.
 * @param nombre Nombre de la temporada.
 * @param fechaInicio Inicio de vigencia; puede ser nulo para la temporada base.
 * @param fechaFin Fin exclusivo de vigencia; puede ser nulo para la temporada base.
 * @param base Indica si es la temporada de respaldo.
 * @param cantidadTarifas Número de tarifas configuradas.
 * @author Equipo SGA
 * @version 1.0
 */
public record TemporadaResponse(
        String id,
        String nombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean base,
        int cantidadTarifas) { }
