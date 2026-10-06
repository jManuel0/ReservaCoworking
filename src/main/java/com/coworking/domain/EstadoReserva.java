package com.coworking.domain;

/**
 * Enum que representa el ciclo de vida de una reserva.
 * Una reserva nace CONFIRMADA y puede pasar a CANCELADA, pero no al revés.
 */
public enum EstadoReserva {
    CONFIRMADA,
    PENDIENTE,
    CANCELADA
}
