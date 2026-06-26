package com.ventaslibros.infrastructure.persistence.repository;

import com.ventaslibros.domain.enums.TipoMovimiento;
import com.ventaslibros.infrastructure.persistence.entity.MovimientoSaldoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoSaldoJpaRepository extends JpaRepository<MovimientoSaldoEntity, Long> {
    List<MovimientoSaldoEntity> findByClienteIdOrderByFechaDesc(Long clienteId);
    List<MovimientoSaldoEntity> findByClienteIdAndTipoOrderByFechaDesc(Long clienteId, TipoMovimiento tipo);
}
