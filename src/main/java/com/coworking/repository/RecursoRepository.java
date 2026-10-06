package com.coworking.repository;

import com.coworking.domain.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    boolean existsByNombre(String nombre);
}
