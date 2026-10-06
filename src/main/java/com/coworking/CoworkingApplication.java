package com.coworking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación ReservasCoworking.
 *
 * @SpringBootApplication es una anotación combinada que activa:
 *   - @Configuration: esta clase puede definir beans de Spring.
 *   - @EnableAutoConfiguration: Spring Boot configura automáticamente
 *     las dependencias del classpath (por ejemplo, Tomcat, JPA, H2).
 *   - @ComponentScan: escanea todos los paquetes bajo com.coworking
 *     buscando componentes (@Service, @Repository, @Controller, etc.).
 *
 * Al ejecutar main(), Spring Boot levanta el servidor Tomcat embebido
 * y deja la aplicación lista para recibir peticiones HTTP.
 */
@SpringBootApplication
public class CoworkingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoworkingApplication.class, args);
    }
}
