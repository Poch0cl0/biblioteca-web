package pe.unitru.biblioteca_api.controller;

import jakarta.validation.Valid;
import pe.unitru.biblioteca_api.dto.PrestamoRequestDTO;
import pe.unitru.biblioteca_api.dto.PrestamoResponseDTO;
import pe.unitru.biblioteca_api.service.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public List<PrestamoResponseDTO> listar() {
        return prestamoService.listar()
                .stream()
                .map(PrestamoResponseDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public PrestamoResponseDTO obtenerPorId(@PathVariable Long id) {
        return PrestamoResponseDTO.fromEntity(prestamoService.obtenerPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoResponseDTO registrar(@Valid @RequestBody PrestamoRequestDTO request) {
        return PrestamoResponseDTO.fromEntity(
                prestamoService.registrarPrestamo(request.usuarioId(), request.libroId())
        );
    }
}
