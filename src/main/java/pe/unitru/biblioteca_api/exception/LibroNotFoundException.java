package pe.unitru.biblioteca_api.exception;

public class LibroNotFoundException extends RuntimeException {

    public LibroNotFoundException(String message) {
        super(message);
    }
}
