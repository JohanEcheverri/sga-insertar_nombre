package co.uniquindio.edu.sga.domain.valueobject;

/**
 * Tipos de movimiento del folio. Los de tipo crédito RESTAN del total de cargos: así una
 * cancelación o un ajuste negativo se registra como movimiento inverso (RN-16) sin editar
 * ni borrar cargos previos.
 */
public enum TipoCargo {
    ALOJAMIENTO(false),
    SERVICIO_ADICIONAL(false),
    AJUSTE_MODIFICACION(false),      // antes AJUSTAE_MODIFICACION (typo)
    PENALIDAD_CANCELACION(false),
    PENALIDAD_NO_SHOW(false),
    AJUSTE_A_FAVOR(true),            // ajuste negativo por modificación (RN-14)
    REVERSO_ALOJAMIENTO(true);       // reverso del alojamiento al cancelar / no-show / vencer

    private final boolean credito;

    TipoCargo(boolean credito) {
        this.credito = credito;
    }

    public boolean esCredito() {
        return credito;
    }
}
