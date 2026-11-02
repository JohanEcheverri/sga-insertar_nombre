package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Dinero(BigDecimal valor) {

    public static final Dinero CERO = new Dinero(BigDecimal.ZERO);

    public Dinero {
        if (valor == null || valor.signum() < 0) {
            throw new ReglaDominioException("El valor monetario no puede ser negativo.");
        }
        valor = valor.setScale(0, RoundingMode.HALF_UP);
    }

    public static Dinero pesos(long valor) {
        return new Dinero(BigDecimal.valueOf(valor));
    }

    public static Dinero de(BigDecimal valor) {
        return new Dinero(valor);
    }

    public Dinero sumar(Dinero otro) {
        if (otro == null) {
            return this;
        }
        return new Dinero(this.valor.add(otro.valor()));
    }

    public Dinero multiplicar(BigDecimal factor) {
        if (factor == null || factor.signum() < 0) {
            throw new ReglaDominioException("El factor monetario no puede ser nulo ni negativo.");
        }
        return new Dinero(valor.multiply(factor));
    }

    public Dinero multiplicarPor(int cantidad) {
        return new Dinero(this.valor.multiply(BigDecimal.valueOf(cantidad)));
    }

    public boolean esPositivo() {
        return valor.signum() > 0;
    }
}