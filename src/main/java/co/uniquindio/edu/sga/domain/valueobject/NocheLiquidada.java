package co.uniquindio.edu.sga.domain.valueobject;

import java.time.LocalDate;

public record NocheLiquidada(
        LocalDate fecha,
        String temporadaId,
        Dinero valorUnitario,
        int cantidadOcupantes,
        Dinero subtotal
) {}