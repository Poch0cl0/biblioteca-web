package pe.unitru.biblioteca_api.service;

import org.springframework.stereotype.Service;
import pe.unitru.biblioteca_api.exception.UsuarioNotFoundException;
import pe.unitru.biblioteca_api.model.Usuario;
import pe.unitru.biblioteca_api.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new UsuarioNotFoundException("Usuario con id " + id + " no encontrado"));
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuarioRepository.delete(usuario);
    }
}