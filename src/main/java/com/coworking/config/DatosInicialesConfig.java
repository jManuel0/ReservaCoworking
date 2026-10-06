package com.coworking.config;

import com.coworking.domain.Cliente;
import com.coworking.domain.Recurso;
import com.coworking.domain.TipoCliente;
import com.coworking.domain.TipoRecurso;
import com.coworking.repository.ClienteRepository;
import com.coworking.repository.RecursoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Configuración que carga datos de ejemplo al iniciar la aplicación.
 *
 * @Configuration: Indica que esta clase contiene configuración de Spring.
 * Puede definir beans y será procesada antes de que la app empiece a
 * recibir peticiones HTTP.
 *
 * CommandLineRunner: Interfaz de Spring Boot. Su método run() se ejecuta
 * automáticamente después de que el ApplicationContext esté listo,
 * justo antes de que el servidor empiece a aceptar conexiones.
 *
 * Solo carga datos si las tablas están vacías, para no duplicar datos
 * al reiniciar la aplicación en modo ddl-auto=update.
 */
@Configuration
public class DatosInicialesConfig implements CommandLineRunner {

    private final RecursoRepository recursoRepository;
    private final ClienteRepository clienteRepository;

    public DatosInicialesConfig(RecursoRepository recursoRepository,
                                ClienteRepository clienteRepository) {
        this.recursoRepository = recursoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        cargarRecursosIniciales();
        cargarClientesIniciales();
    }

    /**
     * Crea los 4 espacios base del coworking si la tabla está vacía.
     * Dos escritorios individuales y dos salas de reuniones.
     */
    private void cargarRecursosIniciales() {
        // Solo cargamos si no hay ningún recurso en la base de datos
        if (recursoRepository.count() == 0) {
            recursoRepository.save(new Recurso(
                "Escritorio A1", TipoRecurso.ESCRITORIO, 1,
                new BigDecimal("8000.00"), false
            ));
            recursoRepository.save(new Recurso(
                "Escritorio A2", TipoRecurso.ESCRITORIO, 1,
                new BigDecimal("8000.00"), false
            ));
            recursoRepository.save(new Recurso(
                "Sala Andes", TipoRecurso.SALA_REUNION, 6,
                new BigDecimal("30000.00"), false
            ));
            recursoRepository.save(new Recurso(
                "Sala Galeras", TipoRecurso.SALA_REUNION, 12,
                new BigDecimal("45000.00"), true
            ));
            System.out.println("✓ Datos iniciales: 4 recursos cargados correctamente.");
        } else {
            System.out.println("✓ Recursos ya existen, no se cargan datos iniciales.");
        }
    }

    /**
     * Crea los 2 clientes base si la tabla está vacía.
     * Un cliente estándar y un cliente VIP con horas de membresía.
     */
    private void cargarClientesIniciales() {
        if (clienteRepository.count() == 0) {
            clienteRepository.save(new Cliente(
                "Laura Gómez", "laura.gomez@email.com",
                TipoCliente.ESTANDAR, 0
            ));
            clienteRepository.save(new Cliente(
                "Carlos Ruiz", "carlos.ruiz@email.com",
                TipoCliente.VIP, 20
            ));
            System.out.println("✓ Datos iniciales: 2 clientes cargados correctamente.");
        } else {
            System.out.println("✓ Clientes ya existen, no se cargan datos iniciales.");
        }
    }
}
