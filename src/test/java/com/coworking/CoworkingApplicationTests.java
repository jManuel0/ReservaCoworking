package com.coworking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Prueba de integración básica que verifica que el contexto de Spring
 * se carga correctamente. Si hay errores de configuración, este test falla.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CoworkingApplicationTests {

    @Test
    void contextLoads() {
        // Si Spring Boot puede iniciar el contexto sin errores, esta prueba pasa.
    }
}
