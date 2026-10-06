package com.coworking.service;

import com.coworking.domain.Cliente;
import com.coworking.domain.EstadoReserva;
import com.coworking.domain.Recurso;
import com.coworking.domain.Reserva;
import com.coworking.dto.request.ReservaRequest;
import com.coworking.dto.response.ReservaResponse;
import com.coworking.mapper.ReservaMapper;
import com.coworking.repository.ClienteRepository;
import com.coworking.repository.RecursoRepository;
import com.coworking.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona las reservas de espacios de coworking.
 *
 * Aplica las siguientes reglas de negocio al crear una reserva:
 *   1. La hora de fin debe ser posterior a la de inicio.
 *   2. La reserva debe estar en el mismo dÃ­a.
 *   3. El horario de operaciÃ³n es de 7:00 a 21:00.
 *   4. No se puede reservar en el pasado.
 *   5. La anticipaciÃ³n mÃ¡xima depende del tipo de cliente (7 o 30 dÃ­as).
 *   6. El recurso no puede estar ya reservado en ese intervalo.
 *
 * TambiÃ©n calcula el costo aplicando horas de membresÃ­a y descuentos VIP.
 */
@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final RecursoRepository recursoRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          ClienteRepository clienteRepository,
                          RecursoRepository recursoRepository) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.recursoRepository = recursoRepository;
    }

    /**
     * Retorna todas las reservas del sistema.
     */
    @Transactional(readOnly = true)
    public List<ReservaResponse> listar() {
        return reservaRepository.findAll()
            .stream()
            .map(ReservaMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Busca una reserva por su ID.
     *
     * @throws ResponseStatusException si no existe
     */
    @Transactional(readOnly = true)
    public ReservaResponse buscarPorId(Long id) {
        Reserva reserva = obtenerReservaOFallar(id);
        return ReservaMapper.toResponse(reserva);
    }

    /**
     * Crea una nueva reserva aplicando todas las validaciones de negocio.
     *
     * Las validaciones se aplican en orden estricto. La primera que falla
     * lanza una excepciÃ³n y no se continÃºa con las siguientes.
     *
     * @throws ResponseStatusException si alguna regla de negocio es violada
     * @throws ResponseStatusException    si el recurso ya estÃ¡ reservado en ese horario
     */
    @Transactional
    public ReservaResponse crear(ReservaRequest req) {
        Cliente cliente = clienteRepository.findById(req.clienteId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente con ID " + req.clienteId() + " no encontrado"));

        Recurso recurso = recursoRepository.findById(req.recursoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso con ID " + req.recursoId() + " no encontrado"));

        validarReserva(req.inicio(), req.fin(), cliente);
        verificarSolapamiento(recurso.getId(), req.inicio(), req.fin());

        // --- CÃ¡lculo del costo ---
        // Calculamos los minutos y los convertimos a horas, redondeando hacia arriba.
        // Ejemplo: 90 minutos â†’ ceil(90/60.0) = ceil(1.5) = 2 horas cobradas.
        long minutos = ChronoUnit.MINUTES.between(req.inicio(), req.fin());
        int horas = (int) Math.ceil(minutos / 60.0);

        // Las horas de membresÃ­a se usan primero, hasta agotar las disponibles.
        int horasDisponibles = cliente.getHorasIncluidasDisponibles();
        int horasIncluidas = Math.min(horas, horasDisponibles);
        int horasCobradas = horas - horasIncluidas;

        // BigDecimal se usa en lugar de double para el dinero porque double tiene
        // errores de precisiÃ³n con nÃºmeros decimales (0.1 + 0.2 â‰  0.3 exactamente).
        // BigDecimal garantiza precisiÃ³n exacta, lo cual es crÃ­tico en sistemas financieros.
        double descuento = cliente.getDescuento();
        BigDecimal costo = recurso.getPrecioHora()
            .multiply(BigDecimal.valueOf(horasCobradas))
            .multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(descuento)))
            .setScale(2, RoundingMode.HALF_UP);

        // Actualizamos las horas usadas del cliente
        cliente.consumirHoras(horasIncluidas);
        clienteRepository.save(cliente);

        // Creamos y guardamos la reserva
        Reserva reserva = new Reserva(
            cliente, recurso, req.inicio(), req.fin(),
            horasIncluidas, costo, EstadoReserva.CONFIRMADA
        );
        Reserva guardada = reservaRepository.save(reserva);
        return ReservaMapper.toResponse(guardada);
    }

    /**
     * Cancela una reserva existente y devuelve las horas de membresÃ­a al cliente.
     *
     * @throws ResponseStatusException si no existe la reserva
     * @throws ResponseStatusException    si la reserva ya estÃ¡ cancelada
     */
    @Transactional
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = obtenerReservaOFallar(id);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La reserva ya estÃ¡ cancelada.");
        }

        // Devolvemos las horas incluidas al cliente
        Cliente cliente = reserva.getCliente();
        cliente.devolverHoras(reserva.getHorasIncluidasUsadas());
        clienteRepository.save(cliente);

        // Cambiamos el estado de la reserva
        reserva.setEstado(EstadoReserva.CANCELADA);
        Reserva cancelada = reservaRepository.save(reserva);
        return ReservaMapper.toResponse(cancelada);
    }

    // =========================================================
    // MÃ‰TODOS PRIVADOS DE VALIDACIÃ“N
    // =========================================================

    /**
     * Aplica todas las validaciones de negocio sobre el intervalo de tiempo
     * y las restricciones del cliente.
     */
    private void validarReserva(LocalDateTime inicio, LocalDateTime fin, Cliente cliente) {
        validarFinMayorQueInicio(inicio, fin);
        validarMismoDia(inicio, fin);
        validarHorarioOperacion(inicio, fin);
        validarNoEnPasado(inicio);
        validarAnticipacionMaxima(inicio, cliente);
    }

    /** Regla 1: El fin debe ser posterior al inicio. */
    private void validarFinMayorQueInicio(LocalDateTime inicio, LocalDateTime fin) {
        if (!fin.isAfter(inicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "La hora de fin debe ser posterior a la hora de inicio.");
        }
    }

    /** Regla 2: La reserva debe estar en el mismo dÃ­a. */
    private void validarMismoDia(LocalDateTime inicio, LocalDateTime fin) {
        if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "La reserva debe comenzar y terminar en el mismo dÃ­a.");
        }
    }

    /** Regla 3: El horario de operaciÃ³n es de 7:00 a 21:00. */
    private void validarHorarioOperacion(LocalDateTime inicio, LocalDateTime fin) {
        int horaInicio = inicio.getHour();
        int horaFin = fin.getHour();
        int minutoFin = fin.getMinute();

        // El inicio debe ser a partir de las 7:00 y antes de las 21:00
        boolean inicioValido = horaInicio >= 7 && horaInicio < 21;
        // El fin puede ser exactamente las 21:00:00 pero no despuÃ©s
        boolean finValido = (horaFin < 21) || (horaFin == 21 && minutoFin == 0);

        if (!inicioValido || !finValido) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Las reservas solo se pueden hacer entre las 7:00 y las 21:00.");
        }
    }

    /** Regla 4: No se puede reservar en el pasado. */
    private void validarNoEnPasado(LocalDateTime inicio) {
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "No se puede crear una reserva en el pasado.");
        }
    }

    /** Regla 5: La anticipaciÃ³n mÃ¡xima depende del tipo de cliente. */
    private void validarAnticipacionMaxima(LocalDateTime inicio, Cliente cliente) {
        int diasMax = cliente.getDiasAnticipacionMax();
        LocalDateTime limiteMaximo = LocalDateTime.now().plusDays(diasMax);
        if (inicio.isAfter(limiteMaximo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Los clientes " + cliente.getTipo().name().toLowerCase() +
                " solo pueden reservar con un mÃ¡ximo de " + diasMax + " dÃ­as de anticipaciÃ³n.");
        }
    }

    /** Regla 6: El recurso no puede estar ya reservado en ese intervalo. */
    private void verificarSolapamiento(Long recursoId, LocalDateTime inicio, LocalDateTime fin) {
        if (reservaRepository.existeSolapamiento(recursoId, inicio, fin)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El recurso ya tiene una reserva confirmada que se solapa con el horario solicitado.");
        }
    }

    /** Busca la reserva por ID o lanza ResponseStatusException. */
    private Reserva obtenerReservaOFallar(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva con ID " + id + " no encontrada"));
    }
}



