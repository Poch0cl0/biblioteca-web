package pe.unitru.biblioteca_api.exception;

public class LibroNoDisponibleException extends RuntimeException {

    public LibroNoDisponibleException(String message) {
        super(message);
    }
}
