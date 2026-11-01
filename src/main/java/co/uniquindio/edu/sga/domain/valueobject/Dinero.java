package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.math.BigDecimal;

public record Dinero(BigDecimal valor) {

    public Dinero {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaDominioException("El valor monetario no puede ser nulo ni negativo.");
        }
    }

    public static Dinero de(double valor) {
        return new Dinero(BigDecimal.valueOf(valor));
    }

    public static Dinero cero() {
        return new Dinero(BigDecimal.ZERO);
    }

    public Dinero sumar(Dinero otro) {
        if (otro == null) {
            return this;
        }
        return new Dinero(this.valor.add(otro.valor()));
    }

    public Dinero multiplicarPor(int cantidad) {
        return new Dinero(this.valor.multiply(BigDecimal.valueOf(cantidad)));
    }
}