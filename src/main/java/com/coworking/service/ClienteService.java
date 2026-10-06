package com.coworking.service;

import com.coworking.domain.Cliente;
import com.coworking.dto.request.ClienteRequest;
import com.coworking.dto.response.ClienteResponse;
import com.coworking.dto.response.ReservaResponse;
import com.coworking.mapper.ClienteMapper;
import com.coworking.mapper.ReservaMapper;
import com.coworking.repository.ClienteRepository;
import com.coworking.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona el ciclo de vida de los clientes:
 * registro, consulta, actualizaciÃ³n y eliminaciÃ³n.
 * TambiÃ©n expone el historial de reservas de un cliente.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ReservaRepository reservaRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          ReservaRepository reservaRepository) {
        this.clienteRepository = clienteRepository;
        this.reservaRepository = reservaRepository;
    }

    /**
     * Retorna la lista completa de clientes registrados.
     */
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
            .stream()
            .map(ClienteMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Busca un cliente por su ID.
     *
     * @throws ResponseStatusException si no existe
     */
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = obtenerClienteOFallar(id);
        return ClienteMapper.toResponse(cliente);
    }

    /**
     * Retorna todas las reservas de un cliente (en cualquier estado).
     *
     * @throws ResponseStatusException si el cliente no existe
     */
    @Transactional(readOnly = true)
    public List<ReservaResponse> listarReservasPorCliente(Long clienteId) {
        obtenerClienteOFallar(clienteId); // Verifica que el cliente exista
        return reservaRepository.findByClienteId(clienteId)
            .stream()
            .map(ReservaMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Registra un nuevo cliente en el sistema.
     *
     * @throws ResponseStatusException si ya existe un cliente con ese email
     */
    @Transactional
    public ClienteResponse crear(ClienteRequest req) {
        if (clienteRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente registrado con el email: " + req.email());
        }
        Cliente nuevo = new Cliente(
            req.nombre(), req.email(), req.tipo(), req.horasMembresia()
        );
        Cliente guardado = clienteRepository.save(nuevo);
        return ClienteMapper.toResponse(guardado);
    }

    /**
     * Actualiza los datos de un cliente existente.
     * Si el email cambia, verifica que el nuevo no estÃ© en uso por otro cliente.
     *
     * @throws ResponseStatusException si no existe el ID
     * @throws ResponseStatusException    si el nuevo email ya estÃ¡ en uso
     */
    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest req) {
        Cliente cliente = obtenerClienteOFallar(id);

        // Solo validamos el email si cambiÃ³
        boolean emailCambio = !cliente.getEmail().equalsIgnoreCase(req.email());
        if (emailCambio && clienteRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El email " + req.email() + " ya estÃ¡ en uso por otro cliente.");
        }

        cliente.setNombre(req.nombre());
        cliente.setEmail(req.email());
        cliente.setTipo(req.tipo());
        cliente.setHorasMembresia(req.horasMembresia());
        Cliente actualizado = clienteRepository.save(cliente);
        return ClienteMapper.toResponse(actualizado);
    }

    /**
     * Elimina un cliente del sistema.
     *
     * @throws ResponseStatusException si no existe el ID
     * @throws ResponseStatusException    si el cliente tiene reservas asociadas
     */
    @Transactional
    public void eliminar(Long id) {
        obtenerClienteOFallar(id);
        if (reservaRepository.existsByClienteId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el cliente porque tiene reservas asociadas.");
        }
        clienteRepository.deleteById(id);
    }

    // =========================================================
    // MÃ‰TODOS PRIVADOS
    // =========================================================

    private Cliente obtenerClienteOFallar(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente con ID " + id + " no encontrado"));
    }
}



