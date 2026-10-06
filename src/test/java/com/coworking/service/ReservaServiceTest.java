package com.coworking.service;

import com.coworking.domain.Cliente;
import com.coworking.domain.EstadoReserva;
import com.coworking.domain.Recurso;
import com.coworking.domain.Reserva;
import com.coworking.domain.TipoCliente;
import com.coworking.domain.TipoRecurso;
import com.coworking.dto.request.ReservaRequest;
import com.coworking.dto.response.ReservaResponse;
import com.coworking.repository.ClienteRepository;
import com.coworking.repository.RecursoRepository;
import com.coworking.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para ReservaService.
 *
 * @ExtendWith(MockitoExtension.class): Activa Mockito en JUnit 5.
 * @Mock: Crea un objeto simulado (mock) del repositorio. No toca la BD real.
 * @InjectMocks: Crea una instancia real de ReservaService inyectando los mocks.
 *
 * Los mocks de cliente y recurso son objetos REALES (new Cliente/Recurso)
 * para que sus métodos de negocio (getDescuento, consumirHoras, etc.) funcionen.
 * Solo los repositorios se mockean porque acceden a la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RecursoRepository recursoRepository;

    @InjectMocks
    private ReservaService reservaService;

    // Objetos reales para que los métodos de negocio funcionen correctamente
    private Cliente clienteEstandar;
    private Cliente clienteVip;
    private Recurso recurso;

    @BeforeEach
    void configurar() {
        // Cliente estándar: sin descuento, sin horas de membresía
        clienteEstandar = new Cliente("Ana Torres", "ana@test.com", TipoCliente.ESTANDAR, 0);
        clienteEstandar.setId(1L);

        // Cliente VIP: 15% descuento, 20 horas de membresía disponibles
        clienteVip = new Cliente("Pedro Soto", "pedro@test.com", TipoCliente.VIP, 20);
        clienteVip.setId(2L);

        // Recurso de prueba: sala de reuniones a $10000/hora
        recurso = new Recurso("Sala Test", TipoRecurso.SALA_REUNION, 6,
                              new BigDecimal("10000.00"), false);
        recurso.setId(1L);
    }

    /**
     * PRUEBA 1: Regla de negocio - Horario de operación.
     * Las reservas solo son válidas entre las 7:00 y las 21:00.
     * Una reserva a las 5:00 AM debe responder con HTTP 400.
     */
    @Test
    @DisplayName("Reserva fuera del horario devuelve error 400")
    void reservaFueraDeHorarioLanzaExcepcion() {
        // Configuramos un cliente estándar que devolverá el repositorio
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEstandar));
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));

        // Creamos una solicitud a las 5:00 AM (fuera del horario permitido)
        ReservaRequest request = new ReservaRequest(
            1L, 1L,
            LocalDateTime.now().plusDays(1).withHour(5).withMinute(0).withSecond(0),
            LocalDateTime.now().plusDays(1).withHour(7).withMinute(0).withSecond(0)
        );

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
            () -> reservaService.crear(request));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    /**
     * PRUEBA 2: Regla de negocio - Sin solapamiento.
     * Si un recurso ya tiene una reserva confirmada en ese horario,
     * debe responder con HTTP 409.
     */
    @Test
    @DisplayName("Solapamiento de horario devuelve error 409")
    void solapamientoDevuelveError409() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEstandar));
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));

        // Simulamos que ya existe una reserva solapada
        when(reservaRepository.existeSolapamiento(anyLong(), any(), any()))
            .thenReturn(true);

        ReservaRequest request = new ReservaRequest(
            1L, 1L,
            LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0),
            LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0)
        );

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
            () -> reservaService.crear(request));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }

    /**
     * PRUEBA 3: Regla de negocio - Descuento y horas de membresía VIP.
     * Un cliente VIP con 20 horas disponibles que reserva 2 horas:
     *   - horasIncluidas = 2 (las cubre la membresía)
     *   - horasCobradas = 0
     *   - costo final = $0.00
     */
    @Test
    @DisplayName("Cliente VIP usa horas de membresía y obtiene costo cero")
    void clienteVipUsaHorasYDescuento() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(clienteVip));
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(reservaRepository.existeSolapamiento(anyLong(), any(), any())).thenReturn(false);

        // Simulamos que el repositorio devuelve la reserva guardada con los datos correctos
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(100L);
            return r;
        });
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation ->
            invocation.getArgument(0));

        ReservaRequest request = new ReservaRequest(
            2L, 1L,
            LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0),
            LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0)
        );

        ReservaResponse response = reservaService.crear(request);

        // Las 2 horas deben cubrirse completamente con la membresía
        assertEquals(2, response.horasIncluidasUsadas(),
            "Debería usar 2 horas de la membresía VIP");
        assertEquals(new BigDecimal("0.00"), response.costo(),
            "El costo debe ser $0.00 porque la membresía cubre las 2 horas");
    }

    /**
     * PRUEBA 4: Regla de negocio - Devolución de horas al cancelar.
     * Cuando se cancela una reserva que usó 2 horas de membresía,
     * esas horas deben devolverse al cliente.
     */
    @Test
    @DisplayName("Cancelar reserva devuelve las horas de membresía al cliente")
    void cancelarReservaDevuelveHoras() {
        // El cliente VIP ya usó 2 horas (horasUsadas = 2)
        clienteVip.consumirHoras(2);
        assertEquals(2, clienteVip.getHorasUsadas(), "Setup: cliente debe tener 2 horas usadas");

        // Creamos una reserva que usó 2 horas de membresía
        Reserva reservaExistente = new Reserva(
            clienteVip, recurso,
            LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0),
            LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0),
            2, // horasIncluidasUsadas
            BigDecimal.ZERO,
            EstadoReserva.CONFIRMADA
        );
        reservaExistente.setId(10L);

        when(reservaRepository.findById(10L)).thenReturn(Optional.of(reservaExistente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));

        reservaService.cancelar(10L);

        // Las horas deben haberse devuelto al cliente
        assertEquals(0, clienteVip.getHorasUsadas(),
            "Después de cancelar, el cliente debe recuperar las 2 horas (horasUsadas = 0)");
        assertEquals(20, clienteVip.getHorasIncluidasDisponibles(),
            "El cliente VIP debe tener nuevamente 20 horas disponibles");
    }
}
