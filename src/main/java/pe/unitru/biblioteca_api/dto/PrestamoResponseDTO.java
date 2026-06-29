package pe.unitru.biblioteca_api.dto;

import pe.unitru.biblioteca_api.model.Prestamo;

import java.time.LocalDate;

public record PrestamoResponseDTO(
        Long id,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        Long usuarioId,
        String usuarioNombre,
        Long libroId,
        String libroTitulo
) {

    public static PrestamoResponseDTO fromEntity(Prestamo prestamo) {
        return new PrestamoResponseDTO(
                prestamo.getId(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getUsuario().getId(),
                prestamo.getUsuario().getNombres() + " " + prestamo.getUsuario().getApellidos(),
                prestamo.getLibro().getId(),
                prestamo.getLibro().getTitulo()
        );
    }
}
