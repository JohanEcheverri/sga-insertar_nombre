package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.application.port.NotificacionPort;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Adaptador local de notificaciones (I-02): no envía nada, solo deja constancia. Sirve para pruebas y para operar sin internet. */
@Component
public class NotificacionEnMemoria implements NotificacionPort {

    private final List<String> enviadas = new ArrayList<>();

    @Override
    public void reservaCreada(Reserva reserva) {
        enviadas.add("CREADA:" + reserva.getId().valor());
    }

    @Override
    public void reservaConfirmada(Reserva reserva) {
        enviadas.add("CONFIRMADA:" + reserva.getId().valor());
    }

    @Override
    public void reservaCancelada(Reserva reserva) {
        enviadas.add("CANCELADA:" + reserva.getId().valor());
    }

    public List<String> getEnviadas() {
        return List.copyOf(enviadas);
    }
}
