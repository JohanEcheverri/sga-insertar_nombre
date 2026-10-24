package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record Dormitorio(String nombre, int cantidadCamas, String tipoCamas) {

    public Dormitorio{
        // Regla de dormitorio: nombre y tipo de camas son obligatorios, y debe existir al menos una cama.
        if ( nombre == null || nombre.isBlank()){
            throw new ReglaDominioException("El nombre del dormitorio no puede estar vacio.");
        }
        if(cantidadCamas <= 0){
            throw new ReglaDominioException("La cantidad de camas en el dormitorio deben ser más que 0.");
        }
        if(tipoCamas==null || tipoCamas.isBlank()){
            throw new ReglaDominioException("Los tipos de cama en el dormitorio no pueden estar vacíos.");
        }
        nombre = nombre.trim();
        tipoCamas = tipoCamas.trim();
    }

}
