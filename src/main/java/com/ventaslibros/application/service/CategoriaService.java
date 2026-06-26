package com.ventaslibros.application.service;

import com.ventaslibros.application.dto.response.CategoriaResponse;
import com.ventaslibros.infrastructure.persistence.entity.CategoriaEntity;
import com.ventaslibros.infrastructure.persistence.repository.CategoriaJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaService {
    private final CategoriaJpaRepository repository;
    public CategoriaService(CategoriaJpaRepository repository) { this.repository = repository; }
    public List<CategoriaResponse> listar() { return repository.findAllByEliminadoFalseOrderByNombreAsc().stream().map(this::toResponse).toList(); }
    private CategoriaResponse toResponse(CategoriaEntity c) { CategoriaResponse r = new CategoriaResponse(); r.setId(c.getId()); r.setNombre(c.getNombre()); r.setEstado(c.getEstado().name()); return r; }
}
