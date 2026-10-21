package co.uniquindio.edu.sga.domain.valueobject;

public enum EstadoReserva {
    PENDIENTE(true),
    CONFIRMADA(true),
    EN_CURSO(true),
    FINALIZADA(false),
    CANCELADA(false),
    NO_SHOW(false);

    private final boolean activa;

    EstadoReserva(boolean activa){
        this.activa = activa;
    }

    //Consulta esta activa PENDIENTE, CONFIRMADA Y EN_CURSO
    public boolean esActiva(){
        return activa;
    }

    //consulta es terminal FINALIZADO, CANCELADA YNO_SHOW
    public boolean esTerminal(){
        return !activa;
    }

    public boolean retieneDisponibilidad(){
        return activa;
    }

}
