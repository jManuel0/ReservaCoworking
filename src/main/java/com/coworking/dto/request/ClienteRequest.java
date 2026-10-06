package com.coworking.dto.request;

import com.coworking.domain.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO para recibir los datos de un cliente desde el cliente HTTP.
 * Las validaciones garantizan que los datos tengan formato correcto
 * antes de llegar al servicio.
 *
 * @PositiveOrZero: el número puede ser 0 o positivo (no negativo).
 */
public record ClienteRequest(

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    String email,

    @NotNull(message = "El tipo de cliente es obligatorio")
    TipoCliente tipo,

    @PositiveOrZero(message = "Las horas de membresía no pueden ser negativas")
    int horasMembresia

) {}
