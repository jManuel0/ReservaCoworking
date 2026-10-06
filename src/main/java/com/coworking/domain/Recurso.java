package com.coworking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Entidad que representa un espacio reservable en el coworking.
 * Puede ser un escritorio individual o una sala de reuniones.
 * Cada recurso tiene un precio por hora y una capacidad máxima de personas.
 *
 * @Entity: Le dice a JPA que esta clase representa una tabla en la base de datos.
 *          Cada instancia de Recurso es una fila en la tabla "recursos".
 */
@Entity
@Table(name = "recursos")
public class Recurso {

    /**
     * @Id: Marca este campo como la clave primaria de la tabla.
     * @GeneratedValue: La base de datos genera el ID automáticamente
     *                  usando una secuencia (IDENTITY = autoincrement).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column: Personaliza la columna.
     * nullable = false → la columna no acepta valores NULL.
     * unique = true → no puede haber dos recursos con el mismo nombre.
     */
    @Column(nullable = false, unique = true)
    private String nombre;

    /**
     * @Enumerated(EnumType.STRING): Guarda el enum como texto ("ESCRITORIO")
     * en lugar de un número (0). Esto hace la base de datos más legible.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRecurso tipo;

    @Column(nullable = false)
    private int capacidad;

    /**
     * Se usa BigDecimal en lugar de double para el dinero porque double tiene
     * errores de precisión con números decimales (0.1 + 0.2 ≠ 0.3 exactamente).
     * BigDecimal garantiza precisión exacta, lo cual es crítico en sistemas financieros.
     */
    @Column(name = "precio_hora", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioHora;

    @Column(name = "tiene_proyector", nullable = false)
    private boolean tieneProyector;

    // =========================================================
    // CONSTRUCTORES
    // =========================================================

    /** Constructor vacío requerido por JPA. */
    public Recurso() {}

    public Recurso(String nombre, TipoRecurso tipo, int capacidad,
                   BigDecimal precioHora, boolean tieneProyector) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precioHora = precioHora;
        this.tieneProyector = tieneProyector;
    }

    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoRecurso getTipo() { return tipo; }
    public void setTipo(TipoRecurso tipo) { this.tipo = tipo; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public BigDecimal getPrecioHora() { return precioHora; }
    public void setPrecioHora(BigDecimal precioHora) { this.precioHora = precioHora; }

    public boolean isTieneProyector() { return tieneProyector; }
    public void setTieneProyector(boolean tieneProyector) { this.tieneProyector = tieneProyector; }
}
