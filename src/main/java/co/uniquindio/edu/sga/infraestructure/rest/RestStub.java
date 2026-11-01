package co.uniquindio.edu.sga.infraestructure.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Respuesta temporal para endpoints cuyo caso de uso aún no está implementado. */
final class RestStub {
    private RestStub() { }

    static ResponseEntity<Void> noImplementado() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
