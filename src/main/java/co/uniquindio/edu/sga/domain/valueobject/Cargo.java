package co.uniquindio.edu.sga.domain.valueobject;
import java.time.LocalDateTime;
public record Cargo(TipoCargo tipocCargo, String concepto, Dinero monto, LocalDateTime fechapago) {
}
