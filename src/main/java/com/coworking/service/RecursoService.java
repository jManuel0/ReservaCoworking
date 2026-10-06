package com.coworking.service;

import com.coworking.domain.Recurso;
import com.coworking.dto.request.RecursoRequest;
import com.coworking.dto.response.RecursoResponse;
import com.coworking.mapper.RecursoMapper;
import com.coworking.repository.RecursoRepository;
import com.coworking.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que contiene la lÃ³gica de negocio para la gestiÃ³n de recursos.
 * ActÃºa como intermediario entre el Controller (que recibe las peticiones HTTP)
 * y el Repository (que accede a la base de datos).
 *
 * @Service: Marca esta clase como un componente de lÃ³gica de negocio.
 * Spring la detecta automÃ¡ticamente y la registra como bean administrado,
 * lo que permite inyectarla en otras clases.
 */
@Service
public class RecursoService {

    private final RecursoRepository recursoRepository;
    private final ReservaRepository reservaRepository;

    /**
     * InyecciÃ³n por constructor: Spring pasa automÃ¡ticamente los repositorios.
     * Es preferible a @Autowired en campo porque facilita los tests unitarios.
     */
    public RecursoService(RecursoRepository recursoRepository,
                          ReservaRepository reservaRepository) {
        this.recursoRepository = recursoRepository;
        this.reservaRepository = reservaRepository;
    }

    /**
     * Retorna la lista completa de recursos registrados.
     */
    @Transactional(readOnly = true)
    public List<RecursoResponse> listar() {
        return recursoRepository.findAll()
            .stream()
            .map(RecursoMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Busca un recurso por su ID.
     *
     * @throws ResponseStatusException si no existe el ID solicitado
     */
    @Transactional(readOnly = true)
    public RecursoResponse buscarPorId(Long id) {
        Recurso recurso = obtenerRecursoOFallar(id);
        return RecursoMapper.toResponse(recurso);
    }

    /**
     * Crea un nuevo recurso en la base de datos.
     *
     * @throws ResponseStatusException si ya existe un recurso con el mismo nombre
     */
    @Transactional
    public RecursoResponse crear(RecursoRequest req) {
        if (recursoRepository.existsByNombre(req.nombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un recurso con el nombre: " + req.nombre());
        }
        Recurso nuevo = new Recurso(
            req.nombre(), req.tipo(), req.capacidad(),
            req.precioHora(), req.tieneProyector()
        );
        Recurso guardado = recursoRepository.save(nuevo);
        return RecursoMapper.toResponse(guardado);
    }

    /**
     * Actualiza todos los campos de un recurso existente.
     *
     * @throws ResponseStatusException si no existe el ID solicitado
     */
    @Transactional
    public RecursoResponse actualizar(Long id, RecursoRequest req) {
        Recurso recurso = obtenerRecursoOFallar(id);
        recurso.setNombre(req.nombre());
        recurso.setTipo(req.tipo());
        recurso.setCapacidad(req.capacidad());
        recurso.setPrecioHora(req.precioHora());
        recurso.setTieneProyector(req.tieneProyector());
        Recurso actualizado = recursoRepository.save(recurso);
        return RecursoMapper.toResponse(actualizado);
    }

    /**
     * Elimina un recurso de la base de datos.
     *
     * @throws ResponseStatusException si no existe el ID solicitado
     * @throws ResponseStatusException    si el recurso tiene reservas asociadas
     */
    @Transactional
    public void eliminar(Long id) {
        obtenerRecursoOFallar(id);
        if (reservaRepository.existsByRecursoId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el recurso porque tiene reservas asociadas.");
        }
        recursoRepository.deleteById(id);
    }

    /**
     * Retorna los recursos disponibles en un intervalo de tiempo dado.
     * Delega la consulta de solapamiento al repositorio.
     */
    // =========================================================
    // MÃ‰TODOS PRIVADOS (auxiliares internos del servicio)
    // =========================================================

    /**
     * Busca el recurso por ID o lanza ResponseStatusException.
     * ExtraÃ­do para no repetir este patrÃ³n en cada mÃ©todo.
     */
    private Recurso obtenerRecursoOFallar(Long id) {
        return recursoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso con ID " + id + " no encontrado"));
    }
}



