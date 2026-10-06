package com.coworking.controller;

import com.coworking.dto.request.ReservaRequest;
import com.coworking.dto.response.ReservaResponse;
import com.coworking.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para la gestión de reservas.
 * Permite crear, consultar y cancelar reservas.
 *
 * No expone un endpoint DELETE porque las reservas nunca se eliminan,
 * solo se cancelan (para mantener el historial completo).
 */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * GET /api/reservas
     * Lista todas las reservas del sistema (confirmadas y canceladas).
     */
    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {
        return ResponseEntity.ok(reservaService.listar());
    }

    /**
     * GET /api/reservas/{id}
     * Obtiene una reserva específica por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    /**
     * POST /api/reservas
     * Crea una nueva reserva aplicando todas las reglas de negocio.
     * Retorna HTTP 201 Created con la reserva creada.
     */
    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request) {
        ReservaResponse response = reservaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PATCH /api/reservas/{id}/cancelar
     * Cancela una reserva confirmada.
     * Se usa PATCH porque es una actualización parcial del estado.
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }
}
