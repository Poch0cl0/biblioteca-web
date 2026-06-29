package pe.unitru.biblioteca_api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.unitru.biblioteca_api.dto.PrestamoRequestDTO;
import pe.unitru.biblioteca_api.exception.GlobalExceptionHandler;
import pe.unitru.biblioteca_api.exception.LibroNoDisponibleException;
import pe.unitru.biblioteca_api.model.Libro;
import pe.unitru.biblioteca_api.model.Prestamo;
import pe.unitru.biblioteca_api.model.Usuario;
import pe.unitru.biblioteca_api.service.PrestamoService;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PrestamoControllerTest {

    private PrestamoService prestamoService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        prestamoService = Mockito.mock(PrestamoService.class);
        PrestamoController prestamoController = new PrestamoController(prestamoService);

        mockMvc = MockMvcBuilders.standaloneSetup(prestamoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registrarPrestamoDebeRetornarCreated() throws Exception {
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

        Prestamo prestamo = Prestamo.builder()
                .id(10L)
                .fechaPrestamo(LocalDate.now())
                .usuario(usuario)
                .libro(libro)
                .build();

        when(prestamoService.registrarPrestamo(1L, 1L)).thenReturn(prestamo);

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioId": 1,
                                  "libroId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.usuarioId").value(1L))
                .andExpect(jsonPath("$.libroId").value(1L));
    }

    @Test
    void registrarPrestamoCuandoLibroNoDisponibleDebeRetornarConflict() throws Exception {
        when(prestamoService.registrarPrestamo(1L, 1L))
                .thenThrow(new LibroNoDisponibleException("El libro 'Clean Code' no esta disponible"));

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioId": 1,
                                  "libroId": 1
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("El libro 'Clean Code' no esta disponible"));
    }
}
