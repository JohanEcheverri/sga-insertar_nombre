package co.uniquindio.edu.sga.application.dto.response;

import java.util.List;

/** Vista de salida con la información pública de un apartamento.
 * @param id Identificador del apartamento.
 * @param identificadorUnico Código legible único del apartamento.
 * @param descripcion Descripción del apartamento.
 * @param aforoMaximo Capacidad máxima de ocupantes.
 * @param estadoOperativo Estado operativo actual.
 * @param dormitorios Resumen textual de los dormitorios registrados.
 * @param disponible Indica si puede recibir una reserva.
 * @author Equipo SGA
 * @version 1.0
 */
public record ApartamentoResponse(
        String id,
        String identificadorUnico,
        String descripcion,
        Integer aforoMaximo,
        String estadoOperativo,
        List<String> dormitorios,
        boolean disponible
) { }
