package co.uniquindio.edu.sga.application.port;

import co.uniquindio.edu.sga.domain.entity.Reserva;

/**
 * Puerto de salida de notificaciones (10.3.2, opción A). El dominio no conoce al proveedor.
 * I-05: su fallo NUNCA puede impedir reservar, cobrar ni cerrar un folio; los casos de uso lo
 * invocan a través de {@code Seguro.ejecutar(...)}. Debe existir una implementación local
 * (en memoria / log) que no necesite internet ni credenciales (I-02).
 */
public interface NotificacionPort {

    void reservaCreada(Reserva reserva);

    void reservaConfirmada(Reserva reserva);

    void reservaCancelada(Reserva reserva);
}
