package co.uniquindio.edu.sga.application.dto.response;

import java.time.LocalDate;

/** Vista de una persona registrada como ocupante de una reserva.
 * @param nombre Nombres del ocupante.
 * @param apellido Apellidos del ocupante.
 * @param documento Número de documento de identidad.
 * @param fechaNacimiento Fecha de nacimiento.
 * @author Equipo SGA
 * @version 1.0
 */
public record OcupanteResponse(
        String nombre,
        String apellido,
        String documento,
        LocalDate fechaNacimiento) { }
