package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record Dormitorio(String nombre, int cantidadCamas, String tipoCamas) {

    public Dormitorio{
        if ( nombre == null || nombre.isBlank()){
            throw new ReglaDominioException("El nombre del dormitorio no puede estar vacio.");
        }
        if(cantidadCamas <= 0){
            throw new ReglaDominioException("La cantidad de camas en el dormitorio deben ser más que 0.");
        }
        if(tipoCamas==null || tipoCamas.isBlank()){
            throw new ReglaDominioException("Los tipos de cama en el dormitorio no pueden estar vacios.");
        }
    }

}
