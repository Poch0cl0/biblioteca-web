package pe.unitru.biblioteca_api.service;

import org.springframework.stereotype.Service;
import pe.unitru.biblioteca_api.exception.LibroNotFoundException;
import pe.unitru.biblioteca_api.model.Libro;
import pe.unitru.biblioteca_api.repository.LibroRepository;

import java.util.List;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public List<Libro> listar() {
        return libroRepository.findAll();
    }

    public Libro obtenerPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() ->
                        new LibroNotFoundException("Libro con id " + id + " no encontrado"));
    }

    public Libro guardar(Libro libro) {
        return libroRepository.save(libro);
    }

    public void eliminar(Long id) {
        Libro libro = obtenerPorId(id);
        libroRepository.delete(libro);
    }
}