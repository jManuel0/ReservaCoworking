package com.coworking.mapper;

import com.coworking.domain.Recurso;
import com.coworking.dto.response.RecursoResponse;

/**
 * Convierte entidades Recurso en DTOs de respuesta.
 * Mantiene la separación entre la capa de dominio y la capa de presentación.
 * Los métodos son estáticos para poder usarlos sin instanciar la clase.
 */
public class RecursoMapper {

    /** Constructor privado: esta clase no debe instanciarse, solo tiene métodos estáticos. */
    private RecursoMapper() {}

    /**
     * Convierte un Recurso (entidad JPA) en un RecursoResponse (DTO).
     *
     * @param recurso entidad que viene de la base de datos
     * @return DTO listo para serializar a JSON
     */
    public static RecursoResponse toResponse(Recurso recurso) {
        return new RecursoResponse(
            recurso.getId(),
            recurso.getNombre(),
            recurso.getTipo(),
            recurso.getCapacidad(),
            recurso.getPrecioHora(),
            recurso.isTieneProyector()
        );
    }
}
