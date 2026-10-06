package com.coworking.repository;

import com.coworking.domain.EstadoReserva;
import com.coworking.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Reserva.
 * Contiene consultas para detectar solapamientos y generar reportes.
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * Verifica si un recurso ya tiene una reserva CONFIRMADA que se solape
     * con el intervalo [inicio, fin) dado.
     *
     * Lógica de solapamiento: dos intervalos [A,B] y [C,D] se solapan si:
     *   A < D  y  B > C
     *
     * @param recursoId ID del recurso que se quiere reservar
     * @param inicio    fecha/hora de inicio de la nueva reserva
     * @param fin       fecha/hora de fin de la nueva reserva
     * @return true si hay solapamiento, false si el recurso está libre
     */
    @Query("SELECT COUNT(r) > 0 FROM Reserva r " +
           "WHERE r.recurso.id = :recursoId " +
           "AND r.estado = com.coworking.domain.EstadoReserva.CONFIRMADA " +
           "AND r.inicio < :fin " +
           "AND r.fin > :inicio")
    boolean existeSolapamiento(@Param("recursoId") Long recursoId,
                               @Param("inicio") LocalDateTime inicio,
                               @Param("fin") LocalDateTime fin);

    /**
     * Retorna todas las reservas de un cliente específico (cualquier estado).
     * Spring Data JPA genera el SQL a partir del nombre del método.
     */
    List<Reserva> findByClienteId(Long clienteId);

    /**
     * Verifica si un cliente tiene al menos una reserva (para proteger el borrado).
     */
    boolean existsByClienteId(Long clienteId);

    /**
     * Verifica si un recurso tiene al menos una reserva (para proteger el borrado).
     */
    boolean existsByRecursoId(Long recursoId);

    /**
     * Retorna todas las reservas que tienen un estado específico.
     * Se usa principalmente para calcular el reporte de ocupación en Java,
     * evitando funciones SQL que podrían no ser compatibles con H2.
     */
    List<Reserva> findByEstado(EstadoReserva estado);
}
