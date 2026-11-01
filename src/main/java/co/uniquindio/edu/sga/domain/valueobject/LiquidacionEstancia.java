package co.uniquindio.edu.sga.domain.valueobject;

import java.util.List;

public record LiquidacionEstancia(List<NocheLiquidada> noches) {

    public LiquidacionEstancia {
        if (noches == null) {
            noches = List.of();
        }
    }

    /** Calcula el valor total de la estancia sumando el subtotal de cada noche liquidada. */
    public Dinero calcularTotal() {
        if (noches.isEmpty()) {
            return null; // O ajusta si manejas un Dinero.cero() en tu dominio
        }

        Dinero total = noches.get(0).subtotal();
        for (int i = 1; i < noches.size(); i++) {
            total = total.sumar(noches.get(i).subtotal());
        }
        return total;
    }
}