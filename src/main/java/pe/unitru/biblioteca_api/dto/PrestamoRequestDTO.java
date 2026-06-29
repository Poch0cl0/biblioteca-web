package pe.unitru.biblioteca_api.dto;

import jakarta.validation.constraints.NotNull;

public record PrestamoRequestDTO(
        @NotNull(message = "El usuarioId es obligatorio")
        Long usuarioId,

        @NotNull(message = "El libroId es obligatorio")
        Long libroId
) {
}
