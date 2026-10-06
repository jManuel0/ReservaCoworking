package com.coworking.mapper;

import com.coworking.domain.Reserva;
import com.coworking.dto.response.ReservaResponse;

/**
 * Convierte entidades Reserva en DTOs de respuesta.
 * Desnormaliza los nombres del cliente y recurso para que el
 * frontend no necesite hacer peticiones adicionales.
 */
public class ReservaMapper {

    private ReservaMapper() {}

    /**
     * Convierte una Reserva (entidad JPA) en un ReservaResponse (DTO).
     * Accede a cliente.getNombre() y recurso.getNombre() — esto funciona
     * porque el servicio usa @Transactional, que mantiene la sesión JPA
     * abierta y permite cargar las relaciones LAZY.
     *
     * @param reserva entidad que viene de la base de datos
     * @return DTO listo para serializar a JSON
     */
    public static ReservaResponse toResponse(Reserva reserva) {
        return new ReservaResponse(
            reserva.getId(),
            reserva.getCliente().getId(),
            reserva.getCliente().getNombre(),
            reserva.getRecurso().getId(),
            reserva.getRecurso().getNombre(),
            reserva.getInicio(),
            reserva.getFin(),
            reserva.getHorasIncluidasUsadas(),
            reserva.getCosto(),
            reserva.getEstado()
        );
    }
}
