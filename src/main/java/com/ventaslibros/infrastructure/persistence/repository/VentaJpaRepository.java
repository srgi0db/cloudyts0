package com.ventaslibros.infrastructure.persistence.repository;

import com.ventaslibros.infrastructure.persistence.entity.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VentaJpaRepository extends JpaRepository<VentaEntity, Long> {
    List<VentaEntity> findAllByOrderByFechaVentaDesc();
    List<VentaEntity> findByClienteEmailAndClienteEliminadoFalseOrderByFechaVentaDesc(String email);
    Optional<VentaEntity> findByIdAndClienteEmailAndClienteEliminadoFalse(Long id, String email);
}
