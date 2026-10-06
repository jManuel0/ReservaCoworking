package com.coworking.repository;

import com.coworking.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Cliente.
 * Hereda de JpaRepository todos los métodos CRUD básicos.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Verifica si ya existe un cliente con ese email.
     * Evita registrar dos clientes con el mismo correo electrónico.
     */
    boolean existsByEmail(String email);

    /**
     * Busca un cliente por su dirección de email.
     * Retorna Optional para manejar el caso en que no exista.
     */
    Optional<Cliente> findByEmail(String email);
}
