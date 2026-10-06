package com.coworking.dto.response;

import com.coworking.domain.TipoRecurso;
import java.math.BigDecimal;

/**
 * DTO de respuesta con todos los datos de un recurso.
 * Se usa para devolver información al cliente HTTP, evitando exponer
 * la entidad JPA directamente (separación de capas).
 */
public record RecursoResponse(
    Long id,
    String nombre,
    TipoRecurso tipo,
    int capacidad,
    BigDecimal precioHora,
    boolean tieneProyector
) {}
