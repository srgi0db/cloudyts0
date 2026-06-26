package com.ventaslibros.infrastructure.persistence.repository;

import com.ventaslibros.domain.enums.EstadoRegistro;
import com.ventaslibros.infrastructure.persistence.entity.LibroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LibroJpaRepository extends JpaRepository<LibroEntity, Long> {
    List<LibroEntity> findAllByEliminadoFalseOrderByTituloAsc();
    Optional<LibroEntity> findByIdAndEliminadoFalse(Long id);
    boolean existsByIsbnAndEliminadoFalse(String isbn);
    boolean existsByIsbnAndIdNotAndEliminadoFalse(String isbn, Long id);
    List<LibroEntity> findAllByCategoriaIdAndEliminadoFalseOrderByTituloAsc(Long categoriaId);

    @Query("""
        SELECT l
        FROM LibroEntity l
        WHERE l.eliminado = false
          AND l.estado = :estado
          AND (:soloConStock = false OR l.stock > 0)
          AND (:categoriaId IS NULL OR l.categoria.id = :categoriaId)
          AND (
              :texto IS NULL OR :texto = ''
              OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))
              OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :texto, '%'))
          )
        ORDER BY l.titulo ASC
    """)
    List<LibroEntity> buscarCatalogo(
            @Param("texto") String texto,
            @Param("categoriaId") Long categoriaId,
            @Param("soloConStock") boolean soloConStock,
            @Param("estado") EstadoRegistro estado
    );
}
