package com.ventaslibros.infrastructure.persistence.repository;

import com.ventaslibros.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmailAndEliminadoFalse(String email);
    boolean existsByEmailAndEliminadoFalse(String email);
    
    List<UsuarioEntity> findAllByEliminadoFalse();
}