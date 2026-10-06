package com.coworking.controller;

import com.coworking.dto.request.ClienteRequest;
import com.coworking.dto.response.ClienteResponse;
import com.coworking.dto.response.ReservaResponse;
import com.coworking.service.ClienteService;
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
 * Controller REST para la gestión de clientes del coworking.
 * Expone endpoints para registrar, consultar, actualizar y eliminar clientes,
 * además de consultar el historial de reservas de un cliente.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * GET /api/clientes
     * Lista todos los clientes registrados.
     */
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        return ResponseEntity.ok(clienteService.listar());
    }

    /**
     * GET /api/clientes/{id}
     * Obtiene un cliente por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    /**
     * GET /api/clientes/{id}/reservas
     * Obtiene el historial de reservas de un cliente específico.
     */
    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<ReservaResponse>> listarReservas(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.listarReservasPorCliente(id));
    }

    /**
     * POST /api/clientes
     * Registra un nuevo cliente en el sistema.
     */
    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/clientes/{id}
     * Actualiza los datos de un cliente existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizar(id, request));
    }

    /**
     * DELETE /api/clientes/{id}
     * Elimina un cliente (solo si no tiene reservas).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
