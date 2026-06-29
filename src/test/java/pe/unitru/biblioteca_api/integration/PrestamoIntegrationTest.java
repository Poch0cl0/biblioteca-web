package pe.unitru.biblioteca_api.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.unitru.biblioteca_api.model.Libro;
import pe.unitru.biblioteca_api.model.Usuario;
import pe.unitru.biblioteca_api.repository.LibroRepository;
import pe.unitru.biblioteca_api.repository.PrestamoRepository;
import pe.unitru.biblioteca_api.repository.UsuarioRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PrestamoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        prestamoRepository.deleteAll();
        libroRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void consultarPrestamoInexistenteDebeRetornarNotFound() throws Exception {
        mockMvc.perform(get("/api/prestamos/500"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Prestamo con id 500 no encontrado"));
    }

    @Test
    void registrarPrestamoConLibroNoDisponibleDebeRetornarConflict() throws Exception {
        Usuario usuario = guardarUsuario();
        Libro libro = guardarLibro(false);

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prestamoJson(usuario.getId(), libro.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("El libro 'Clean Code' no esta disponible"));
    }

    @Test
    void registrarPrestamoConUsuarioInexistenteDebeRetornarNotFound() throws Exception {
        Libro libro = guardarLibro(true);

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prestamoJson(999L, libro.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuario con id 999 no encontrado"));
    }

    @Test
    void registrarPrestamoConLibroInexistenteDebeRetornarNotFound() throws Exception {
        Usuario usuario = guardarUsuario();

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prestamoJson(usuario.getId(), 999L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Libro con id 999 no encontrado"));
    }

    @Test
    void registrarPrestamoValidoDebeRetornarCreated() throws Exception {
        Usuario usuario = guardarUsuario();
        Libro libro = guardarLibro(true);

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prestamoJson(usuario.getId(), libro.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(usuario.getId()))
                .andExpect(jsonPath("$.libroId").value(libro.getId()))
                .andExpect(jsonPath("$.libroTitulo").value("Clean Code"));
    }

    private Usuario guardarUsuario() {
        return usuarioRepository.save(Usuario.builder()
                .nombres("Ana")
                .apellidos("Perez")
                .correo("ana@example.com")
                .telefono("999888777")
                .build());
    }

    private Libro guardarLibro(boolean disponible) {
        return libroRepository.save(Libro.builder()
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .editorial("Prentice Hall")
                .anioPublicacion(2008)
                .disponible(disponible)
                .build());
    }

    private String prestamoJson(Long usuarioId, Long libroId) {
        return """
                {
                  "usuarioId": %d,
                  "libroId": %d
                }
                """.formatted(usuarioId, libroId);
    }
}
