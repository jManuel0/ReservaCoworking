package com.coworking.dto.request;

import com.coworking.domain.TipoRecurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) para recibir los datos de un recurso desde el cliente HTTP.
 *
 * Un record en Java es una clase especial e inmutable que genera automáticamente:
 *   - Constructor con todos los parámetros
 *   - Getters (nombre(), tipo(), etc.)
 *   - equals(), hashCode() y toString()
 *
 * Es perfecto para DTOs porque solo transportan datos y no necesitan lógica.
 *
 * Anotaciones de validación de Jakarta (se activan con @Valid en el controller):
 *   @NotBlank: el campo String no puede ser nulo, vacío ni solo espacios.
 *   @NotNull: el campo no puede ser nulo (funciona para cualquier tipo).
 *   @Positive: el número debe ser estrictamente mayor a 0.
 */
public record RecursoRequest(

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotNull(message = "El tipo es obligatorio")
    TipoRecurso tipo,

    @Positive(message = "La capacidad debe ser mayor a 0")
    int capacidad,

    @NotNull(message = "El precio por hora es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    BigDecimal precioHora,

    boolean tieneProyector

) {}
