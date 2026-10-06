package com.coworking.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO para solicitar la creación de una reserva.
 * El cliente HTTP envía los IDs del cliente y recurso, junto con
 * el intervalo de tiempo deseado.
 */
public record ReservaRequest(

    @NotNull(message = "El id del cliente es obligatorio")
    Long clienteId,

    @NotNull(message = "El id del recurso es obligatorio")
    Long recursoId,

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    LocalDateTime inicio,

    @NotNull(message = "La fecha y hora de fin es obligatoria")
    LocalDateTime fin

) {}
