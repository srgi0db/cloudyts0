package com.ventaslibros.application.service;

import com.ventaslibros.application.dto.request.LibroRequest;
import com.ventaslibros.application.dto.response.CatalogoLibroResponse;
import com.ventaslibros.application.dto.response.LibroResponse;
import com.ventaslibros.application.exception.RecursoNoEncontradoException;
import com.ventaslibros.application.exception.ReglaNegocioException;
import com.ventaslibros.domain.enums.EstadoRegistro;
import com.ventaslibros.infrastructure.persistence.entity.CategoriaEntity;
import com.ventaslibros.infrastructure.persistence.entity.LibroEntity;
import com.ventaslibros.infrastructure.persistence.repository.CategoriaJpaRepository;
import com.ventaslibros.infrastructure.persistence.repository.LibroJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class LibroService {
    private final LibroJpaRepository libroRepository;
    private final CategoriaJpaRepository categoriaRepository;

    public LibroService(LibroJpaRepository libroRepository, CategoriaJpaRepository categoriaRepository) {
        this.libroRepository = libroRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> listar() {
        return libroRepository.findAllByEliminadoFalseOrderByTituloAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogoLibroResponse> listarCatalogo(String texto, Long categoriaId, Boolean soloConStock) {
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
        boolean filtrarStock = soloConStock == null || soloConStock;

        return libroRepository.findAllByEliminadoFalseOrderByTituloAsc().stream()
                .filter(libro -> libro.getEstado() == EstadoRegistro.ACTIVO)
                .filter(libro -> !filtrarStock || libro.getStock() > 0)
                .filter(libro -> categoriaId == null || libro.getCategoria().getId().equals(categoriaId))
                .filter(libro -> filtro.isBlank()
                        || libro.getTitulo().toLowerCase().contains(filtro)
                        || libro.getAutor().toLowerCase().contains(filtro))
                .map(this::toCatalogoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LibroResponse buscarPorId(Long id) {
        return toResponse(obtenerLibro(id));
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> buscarPorCategoria(Long categoriaId) {
        return libroRepository.findAllByCategoriaIdAndEliminadoFalseOrderByTituloAsc(categoriaId).stream().map(this::toResponse).toList();
    }

    public LibroResponse registrar(LibroRequest request) {
        if (libroRepository.existsByIsbnAndEliminadoFalse(request.getIsbn())) {
            throw new ReglaNegocioException("El ISBN ya se encuentra registrado");
        }
        LibroEntity libro = new LibroEntity();
        aplicarDatos(libro, request);
        libro.setEstado(EstadoRegistro.ACTIVO);
        libro.setEliminado(false);
        return toResponse(libroRepository.save(libro));
    }

    public LibroResponse actualizar(Long id, LibroRequest request) {
        LibroEntity libro = obtenerLibro(id);
        if (libroRepository.existsByIsbnAndIdNotAndEliminadoFalse(request.getIsbn(), id)) {
            throw new ReglaNegocioException("El ISBN ya se encuentra registrado en otro libro");
        }
        aplicarDatos(libro, request);
        return toResponse(libroRepository.save(libro));
    }

    public void eliminar(Long id) {
        LibroEntity libro = obtenerLibro(id);
        libro.setEliminado(true);
        libro.setEstado(EstadoRegistro.INACTIVO);
        libroRepository.save(libro);
    }

    void descontarStock(LibroEntity libro, Integer cantidad) {
        if (libro.getStock() < cantidad) {
            throw new ReglaNegocioException("Cantidad no disponible para este libro");
        }
        libro.setStock(libro.getStock() - cantidad);
        libroRepository.save(libro);
    }

    private void aplicarDatos(LibroEntity libro, LibroRequest request) {
        CategoriaEntity categoria = categoriaRepository.findByIdAndEliminadoFalseAndEstado(request.getCategoriaId(), EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new ReglaNegocioException("La categoría no existe o está inactiva"));
        libro.setIsbn(request.getIsbn());
        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setEditorial(request.getEditorial());
        libro.setCategoria(categoria);
        libro.setPrecioUnitario(request.getPrecioUnitario());
        libro.setStock(request.getStock());
        libro.setUrlImagen(request.getUrlImagen());
    }

    LibroEntity obtenerLibro(Long id) {
        return libroRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado"));
    }

    private String disponibilidad(LibroEntity libro) {
        if (libro.getStock() <= 0) return "No disponible";
        if (libro.getStock() <= 3) return "Últimas unidades";
        return "Disponible";
    }

    private CatalogoLibroResponse toCatalogoResponse(LibroEntity libro) {
        CatalogoLibroResponse r = new CatalogoLibroResponse();
        r.setId(libro.getId());
        r.setTitulo(libro.getTitulo());
        r.setAutor(libro.getAutor());
        r.setCategoriaId(libro.getCategoria().getId());
        r.setCategoriaNombre(libro.getCategoria().getNombre());
        r.setPrecioUnitario(libro.getPrecioUnitario());
        r.setUrlImagen(libro.getUrlImagen());
        r.setDisponible(libro.getStock() > 0);
        r.setDisponibilidad(disponibilidad(libro));
        return r;
    }

    private LibroResponse toResponse(LibroEntity libro) {
        LibroResponse r = new LibroResponse();
        r.setId(libro.getId());
        r.setIsbn(libro.getIsbn());
        r.setTitulo(libro.getTitulo());
        r.setAutor(libro.getAutor());
        r.setEditorial(libro.getEditorial());
        r.setCategoriaId(libro.getCategoria().getId());
        r.setCategoriaNombre(libro.getCategoria().getNombre());
        r.setPrecioUnitario(libro.getPrecioUnitario());
        r.setStock(libro.getStock());
        r.setUrlImagen(libro.getUrlImagen());
        r.setEstado(libro.getEstado().name());
        return r;
    }
}
