package com.coworking.dto.response;

import com.coworking.domain.TipoCliente;

/**
 * DTO de respuesta con todos los datos de un cliente.
 * Incluye las horas disponibles calculadas, útil para que el frontend
 * muestre cuántas horas de membresía le quedan al cliente.
 */
public record ClienteResponse(
    Long id,
    String nombre,
    String email,
    TipoCliente tipo,
    int horasMembresia,
    int horasUsadas,
    int horasDisponibles
) {}
