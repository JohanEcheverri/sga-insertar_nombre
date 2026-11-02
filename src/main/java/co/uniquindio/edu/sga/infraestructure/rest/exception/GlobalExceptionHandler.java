package co.uniquindio.edu.sga.infraestructure.rest.exception;

import co.uniquindio.edu.sga.application.exception.ApplicationException;
import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.exception.RecursoDuplicadoException;
import co.uniquindio.edu.sga.domain.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;

/** Convierte excepciones de la aplicación en respuestas HTTP JSON consistentes. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> manejarAccesoDenegado(
            AccesoDenegadoException exception, HttpServletRequest request) {
        return crearRespuesta(HttpStatus.FORBIDDEN, exception.getMessage(), request);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoDuplicado(
            RecursoDuplicadoException exception, HttpServletRequest request) {
        return crearRespuesta(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        String mensaje = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst().orElse("La solicitud contiene datos inválidos.");
        return crearRespuesta(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> manejarExcepcionAplicacion(
            ApplicationException exception, HttpServletRequest request) {
        return crearRespuesta(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> manejarExcepcionDominio(
            DomainException exception, HttpServletRequest request) {
        return crearRespuesta(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarExcepcionGeneral(
            Exception exception, HttpServletRequest request) {
        return crearRespuesta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor.", request);
    }

    private ResponseEntity<ErrorResponse> crearRespuesta(
            HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
