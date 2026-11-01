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

    // Consulta si está activa (PENDIENTE, CONFIRMADA Y EN_CURSO)
    public boolean esActiva(){
        return activa;
    }

    // Consulta si es terminal (FINALIZADA, CANCELADA Y NO_SHOW)
    public boolean esTerminal(){
        return !activa;
    }

    public boolean retieneDisponibilidad(){
        return activa;
    }

    /** Válida si el estado actual permite transicionar hacia un nuevo estado. */
    public boolean puedeTransitarA(EstadoReserva nuevoEstado) {
        if (nuevoEstado == null) return false;

        switch (this) {
            case PENDIENTE:
                return nuevoEstado == CONFIRMADA || nuevoEstado == CANCELADA || nuevoEstado == NO_SHOW;
            case CONFIRMADA:
                return nuevoEstado == EN_CURSO || nuevoEstado == CANCELADA || nuevoEstado == NO_SHOW;
            case EN_CURSO:
                return nuevoEstado == FINALIZADA;
            case FINALIZADA:
            case CANCELADA:
            case NO_SHOW:
                return false; // Los estados terminales no pueden transicionar a ningún otro estado
            default:
                return false;
        }
    }
}