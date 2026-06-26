package com.ventaslibros.infrastructure.persistence.repository;

import com.ventaslibros.domain.enums.EstadoRegistro;
import com.ventaslibros.infrastructure.persistence.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Long> {
    List<CategoriaEntity> findAllByEliminadoFalseOrderByNombreAsc();
    Optional<CategoriaEntity> findByIdAndEliminadoFalseAndEstado(Long id, EstadoRegistro estado);
}
