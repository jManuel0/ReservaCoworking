package com.coworking.dto.response;

import com.coworking.domain.EstadoReserva;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta con todos los datos de una reserva.
 * Incluye los nombres del cliente y recurso para que el frontend
 * no necesite hacer consultas adicionales.
 */
public record ReservaResponse(
    Long id,
    Long clienteId,
    String clienteNombre,
    Long recursoId,
    String recursoNombre,
    LocalDateTime inicio,
    LocalDateTime fin,
    int horasIncluidasUsadas,
    BigDecimal costo,
    EstadoReserva estado
) {}
