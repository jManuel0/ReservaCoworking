package com.coworking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la reserva de un recurso por parte de un cliente.
 * Contiene el intervalo de tiempo (inicio-fin), el costo calculado y
 * las horas de membresía que se usaron para cubrirla (si aplica).
 */
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @ManyToOne(fetch = FetchType.LAZY): Muchas reservas pertenecen a un cliente.
     * LAZY significa que el cliente NO se carga de la BD hasta que se acceda
     * explícitamente. Esto es más eficiente que cargar todo en cada consulta.
     *
     * @JoinColumn(name = "cliente_id"): Nombre de la columna de clave foránea.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** Igual que cliente: muchas reservas pertenecen a un recurso. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurso_id", nullable = false)
    private Recurso recurso;

    /** Fecha y hora de inicio de la reserva (inclusive). */
    @Column(nullable = false)
    private LocalDateTime inicio;

    /** Fecha y hora de fin de la reserva (exclusive). */
    @Column(nullable = false)
    private LocalDateTime fin;

    /**
     * Horas de membresía que se usaron para cubrir parte del costo.
     * Si el cliente no es VIP o no tenía horas disponibles, este valor es 0.
     */
    @Column(name = "horas_incluidas_usadas", nullable = false)
    private int horasIncluidasUsadas;

    /**
     * Costo final de la reserva en pesos, ya aplicado el descuento
     * y descontadas las horas incluidas de la membresía.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    // =========================================================
    // CONSTRUCTORES
    // =========================================================

    /** Constructor vacío requerido por JPA. */
    public Reserva() {}

    public Reserva(Cliente cliente, Recurso recurso, LocalDateTime inicio,
                   LocalDateTime fin, int horasIncluidasUsadas,
                   BigDecimal costo, EstadoReserva estado) {
        this.cliente = cliente;
        this.recurso = recurso;
        this.inicio = inicio;
        this.fin = fin;
        this.horasIncluidasUsadas = horasIncluidasUsadas;
        this.costo = costo;
        this.estado = estado;
    }

    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Recurso getRecurso() { return recurso; }
    public void setRecurso(Recurso recurso) { this.recurso = recurso; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    public int getHorasIncluidasUsadas() { return horasIncluidasUsadas; }
    public void setHorasIncluidasUsadas(int horasIncluidasUsadas) {
        this.horasIncluidasUsadas = horasIncluidasUsadas;
    }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
}
