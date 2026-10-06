package com.coworking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que representa a un cliente que puede hacer reservas.
 * Existen dos tipos: ESTANDAR (sin beneficios adicionales) y VIP
 * (con descuento del 15% y posibilidad de tener horas de membresía incluidas).
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    /** El email es único: no puede haber dos clientes con el mismo correo. */
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipo;

    /**
     * Horas de membresía que el cliente tiene incluidas en su plan.
     * Para clientes ESTANDAR este valor siempre es 0.
     * Para clientes VIP representa las horas prepagadas totales.
     */
    @Column(name = "horas_membresia", nullable = false)
    private int horasMembresia;

    /**
     * Horas de membresía que ya han sido consumidas en reservas.
     * Empieza en 0 y sube con cada reserva que usa horas incluidas.
     */
    @Column(name = "horas_usadas", nullable = false)
    private int horasUsadas = 0;

    // =========================================================
    // CONSTRUCTORES
    // =========================================================

    /** Constructor vacío requerido por JPA. */
    public Cliente() {}

    public Cliente(String nombre, String email, TipoCliente tipo, int horasMembresia) {
        this.nombre = nombre;
        this.email = email;
        this.tipo = tipo;
        this.horasMembresia = horasMembresia;
        this.horasUsadas = 0;
    }

    // =========================================================
    // MÉTODOS DE NEGOCIO
    // Encapsulan reglas del dominio directamente en la entidad.
    // =========================================================

    /**
     * Retorna el porcentaje de descuento del cliente.
     * VIP tiene 15% de descuento (0.15); ESTANDAR no tiene descuento (0.0).
     */
    public double getDescuento() {
        return tipo == TipoCliente.VIP ? 0.15 : 0.0;
    }

    /**
     * Retorna la anticipación máxima (en días) con la que este cliente
     * puede hacer una reserva. VIP puede reservar hasta 30 días antes;
     * ESTANDAR solo hasta 7 días antes.
     */
    public int getDiasAnticipacionMax() {
        return tipo == TipoCliente.VIP ? 30 : 7;
    }

    /**
     * Retorna las horas de membresía que aún están disponibles para usar.
     * Fórmula: horasMembresia - horasUsadas.
     * Para clientes ESTANDAR, horasMembresia = 0, así que siempre devuelve 0.
     */
    public int getHorasIncluidasDisponibles() {
        return horasMembresia - horasUsadas;
    }

    /**
     * Registra el consumo de horas de membresía al confirmar una reserva.
     *
     * @param horas número de horas incluidas que se usarán en la reserva.
     */
    public void consumirHoras(int horas) {
        this.horasUsadas += horas;
    }

    /**
     * Devuelve las horas de membresía al cliente cuando una reserva es cancelada.
     * Se garantiza que horasUsadas nunca baje de 0.
     *
     * @param horas número de horas a devolver.
     */
    public void devolverHoras(int horas) {
        this.horasUsadas = Math.max(0, this.horasUsadas - horas);
    }

    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public TipoCliente getTipo() { return tipo; }
    public void setTipo(TipoCliente tipo) { this.tipo = tipo; }

    public int getHorasMembresia() { return horasMembresia; }
    public void setHorasMembresia(int horasMembresia) { this.horasMembresia = horasMembresia; }

    public int getHorasUsadas() { return horasUsadas; }
    public void setHorasUsadas(int horasUsadas) { this.horasUsadas = horasUsadas; }
}
