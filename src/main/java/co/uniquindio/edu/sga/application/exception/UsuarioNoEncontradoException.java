package co.uniquindio.edu.sga.application.exception;

public class UsuarioNoEncontradoException extends ApplicationException {

    public UsuarioNoEncontradoException(String id) {
        super(String.format("No se encontró el usuario con el identificador: %s", id));
    }

    public UsuarioNoEncontradoException(String id, Throwable cause) {
        super(String.format("No se encontró el usuario con el identificador: %s", id), cause);
    }
}