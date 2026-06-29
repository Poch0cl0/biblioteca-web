package pe.unitru.biblioteca_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unitru.biblioteca_api.exception.LibroNoDisponibleException;
import pe.unitru.biblioteca_api.exception.LibroNotFoundException;
import pe.unitru.biblioteca_api.exception.PrestamoNotFoundException;
import pe.unitru.biblioteca_api.exception.UsuarioNotFoundException;
import pe.unitru.biblioteca_api.model.Libro;
import pe.unitru.biblioteca_api.model.Prestamo;
import pe.unitru.biblioteca_api.model.Usuario;
import pe.unitru.biblioteca_api.repository.LibroRepository;
import pe.unitru.biblioteca_api.repository.PrestamoRepository;
import pe.unitru.biblioteca_api.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(
            PrestamoRepository prestamoRepository,
            UsuarioRepository usuarioRepository,
            LibroRepository libroRepository) {

        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    public List<Prestamo> listar() {
        return prestamoRepository.findAll();
    }

    public Prestamo obtenerPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() ->
                        new PrestamoNotFoundException(
                                "Prestamo con id " + id + " no encontrado"));
    }

    @Transactional
    public Prestamo registrarPrestamo(Long usuarioId, Long libroId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new UsuarioNotFoundException(
                                "Usuario con id " + usuarioId + " no encontrado"));

        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() ->
                        new LibroNotFoundException(
                                "Libro con id " + libroId + " no encontrado"));

        if (!libro.getDisponible()) {
            throw new LibroNoDisponibleException(
                    "El libro '" + libro.getTitulo() + "' no esta disponible");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);

        libro.setDisponible(false);
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }

    public void eliminar(Long id) {

        Prestamo prestamo = obtenerPorId(id);

        Libro libro = prestamo.getLibro();
        libro.setDisponible(true);
        libroRepository.save(libro);

        prestamoRepository.delete(prestamo);
    }
}
