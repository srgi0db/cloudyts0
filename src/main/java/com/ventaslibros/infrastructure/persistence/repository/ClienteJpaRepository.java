package com.ventaslibros.infrastructure.persistence.repository;

import com.ventaslibros.infrastructure.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
    List<ClienteEntity> findAllByEliminadoFalseOrderByApellidosAscNombresAsc();
    Optional<ClienteEntity> findByIdAndEliminadoFalse(Long id);
    boolean existsByNumeroDocumentoAndEliminadoFalse(String numeroDocumento);
    Optional<ClienteEntity> findByEmailAndEliminadoFalse(String email);
    
    BigDecimal findSaldoById(Long id);
}
