package co.uniquindio.edu.sga.application.command;

import co.uniquindio.edu.sga.domain.valueobject.Dormitorio;

import java.util.ArrayList;
import java.util.List;

public record DormitorioDatos(String nombre, int cantidadCamas, String tipoCamas) {

    public Dormitorio aDominio() {
        return new Dormitorio(nombre, cantidadCamas, tipoCamas);
    }

    public static List<Dormitorio> aDominio(List<DormitorioDatos> datos) {
        List<Dormitorio> lista = new ArrayList<>();
        for (DormitorioDatos d : datos) lista.add(d.aDominio());
        return lista;
    }
}
