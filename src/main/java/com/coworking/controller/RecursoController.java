package com.coworking.controller;

import com.coworking.dto.request.RecursoRequest;
import com.coworking.dto.response.RecursoResponse;
import com.coworking.service.RecursoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para la gestiÃ³n de recursos (espacios) del coworking.
 *
 * @RestController: Combina @Controller y @ResponseBody. Cada mÃ©todo devuelve
 *                 datos (JSON) directamente, no una vista HTML.
 * @RequestMapping: Define la URL base para todos los endpoints de esta clase.
 *
 * Nunca devolvemos entidades JPA directamente: siempre usamos DTOs.
 * La inyecciÃ³n de dependencias se hace por constructor.
 */
@RestController
@RequestMapping("/api/recursos")
public class RecursoController {

    private final RecursoService recursoService;

    public RecursoController(RecursoService recursoService) {
        this.recursoService = recursoService;
    }

    /**
     * GET /api/recursos
     * Lista todos los recursos del coworking.
     */
    @GetMapping
    public ResponseEntity<List<RecursoResponse>> listar() {
        return ResponseEntity.ok(recursoService.listar());
    }

    /**
     * GET /api/recursos/{id}
     * Obtiene un recurso especÃ­fico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecursoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recursoService.buscarPorId(id));
    }

    /**
     * GET /api/recursos/disponibles?inicio=...&fin=...
     * Lista los recursos disponibles en el intervalo de tiempo dado.
     * Formato esperado: 2026-10-06T09:00:00
     *
     * @DateTimeFormat(iso = DATE_TIME): Le dice a Spring cÃ³mo convertir
     * el parÃ¡metro de texto a LocalDateTime.
     */
    /**
     * POST /api/recursos
     * Crea un nuevo recurso.
     *
     * @Valid: Activa las validaciones declaradas en RecursoRequest.
     *        Si fallan, Spring lanza MethodArgumentNotValidException
     *        que Spring responde con HTTP 400.
     */
    @PostMapping
    public ResponseEntity<RecursoResponse> crear(@Valid @RequestBody RecursoRequest request) {
        RecursoResponse response = recursoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/recursos/{id}
     * Actualiza todos los campos de un recurso existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RecursoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RecursoRequest request) {
        return ResponseEntity.ok(recursoService.actualizar(id, request));
    }

    /**
     * DELETE /api/recursos/{id}
     * Elimina un recurso (solo si no tiene reservas).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

