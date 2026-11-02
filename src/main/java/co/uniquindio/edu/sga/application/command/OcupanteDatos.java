package co.uniquindio.edu.sga.application.command;

import co.uniquindio.edu.sga.domain.valueobject.DocumentoIdentidad;
import co.uniquindio.edu.sga.domain.valueobject.FechaNacimiento;
import co.uniquindio.edu.sga.domain.valueobject.NombreCompleto;
import co.uniquindio.edu.sga.domain.valueobject.Ocupante;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record OcupanteDatos(String nombre, String apellido, String documento, LocalDate fechaNacimiento) {

    public Ocupante aDominio() {
        return new Ocupante(new NombreCompleto(nombre, apellido), new DocumentoIdentidad(documento),
                new FechaNacimiento(fechaNacimiento));
    }

    /** Lista mutable a propósito (ArrayList): evita el NPE de List.of(...).contains(null) en validaciones antiguas. */
    public static List<Ocupante> aDominio(List<OcupanteDatos> datos) {
        List<Ocupante> lista = new ArrayList<>();
        for (OcupanteDatos d : datos) lista.add(d.aDominio());
        return lista;
    }
}
