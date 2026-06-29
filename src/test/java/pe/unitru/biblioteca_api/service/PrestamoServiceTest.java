package pe.unitru.biblioteca_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.unitru.biblioteca_api.exception.LibroNoDisponibleException;
import pe.unitru.biblioteca_api.model.Libro;
import pe.unitru.biblioteca_api.model.Prestamo;
import pe.unitru.biblioteca_api.model.Usuario;
import pe.unitru.biblioteca_api.repository.LibroRepository;
import pe.unitru.biblioteca_api.repository.PrestamoRepository;
import pe.unitru.biblioteca_api.repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LibroRepository libroRepository;

    private PrestamoService prestamoService;

    @BeforeEach
    void setUp() {
        prestamoService = new PrestamoService(prestamoRepository, usuarioRepository, libroRepository);
    }

    @Test
    void registrarPrestamoCuandoLibroDisponibleDebeGuardarPrestamo() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombres("Ana")
                .apellidos("Perez")
                .correo("ana@example.com")
                .build();

        Libro libro = Libro.builder()
                .id(1L)
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .disponible(true)
                .build();

        Prestamo prestamoGuardado = Prestamo.builder()
                .id(1L)
                .usuario(usuario)
                .libro(libro)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoGuardado);

        Prestamo resultado = prestamoService.registrarPrestamo(1L, 1L);

        assertEquals(1L, resultado.getId());
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(libro, resultado.getLibro());
        assertFalse(libro.getDisponible());
        verify(libroRepository).save(libro);
        verify(prestamoRepository).save(any(Prestamo.class));
    }

    @Test
    void registrarPrestamoCuandoLibroNoDisponibleDebeLanzarExcepcion() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombres("Ana")
                .apellidos("Perez")
                .correo("ana@example.com")
                .build();

        Libro libro = Libro.builder()
                .id(1L)
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .disponible(false)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

        LibroNoDisponibleException exception = assertThrows(
                LibroNoDisponibleException.class,
                () -> prestamoService.registrarPrestamo(1L, 1L)
        );

        assertNotNull(exception.getMessage());
        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }
}
