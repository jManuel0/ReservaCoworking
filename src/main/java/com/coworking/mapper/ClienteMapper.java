package com.coworking.mapper;

import com.coworking.domain.Cliente;
import com.coworking.dto.response.ClienteResponse;

/**
 * Convierte entidades Cliente en DTOs de respuesta.
 * Calcula las horas disponibles llamando al método de negocio
 * del cliente, manteniendo esa lógica en la entidad.
 */
public class ClienteMapper {

    private ClienteMapper() {}

    /**
     * Convierte un Cliente (entidad JPA) en un ClienteResponse (DTO).
     * Incluye las horas disponibles calculadas en tiempo real.
     *
     * @param cliente entidad que viene de la base de datos
     * @return DTO listo para serializar a JSON
     */
    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
            cliente.getId(),
            cliente.getNombre(),
            cliente.getEmail(),
            cliente.getTipo(),
            cliente.getHorasMembresia(),
            cliente.getHorasUsadas(),
            cliente.getHorasIncluidasDisponibles()
        );
    }
}
