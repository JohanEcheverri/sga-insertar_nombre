package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record TramoCancelacion(int diasMinimosAntelacion, double porcentajeRetencion) {

    public TramoCancelacion{
        if(diasMinimosAntelacion < 0) throw new ReglaDominioException("Ingrese una cantidad de dias minimos de antelacion mayores o iguales a 0.");
        if(porcentajeRetencion < 0 || porcentajeRetencion > 1) throw new ReglaDominioException("Ingrese un porcentaje de retencion entre 0 y 1.");
    }

}
