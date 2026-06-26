package com.ventaslibros.interfaces.rest;

import com.ventaslibros.application.dto.common.ApiResponse;
import com.ventaslibros.application.dto.request.LibroRequest;
import com.ventaslibros.application.dto.response.CatalogoLibroResponse;
import com.ventaslibros.application.dto.response.LibroResponse;
import com.ventaslibros.application.service.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {
    private final LibroService service;
    public LibroController(LibroService service) { this.service = service; }

    @GetMapping("/catalogo")
    public ResponseEntity<ApiResponse<List<CatalogoLibroResponse>>> catalogo(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "true") Boolean soloConStock) {
        return ResponseEntity.ok(ApiResponse.ok("Catálogo listado correctamente", service.listarCatalogo(texto, categoriaId, soloConStock)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LibroResponse>>> listar() { return ResponseEntity.ok(ApiResponse.ok("Libros listados correctamente", service.listar())); }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroResponse>> buscar(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.ok("Libro encontrado", service.buscarPorId(id))); }
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<ApiResponse<List<LibroResponse>>> porCategoria(@PathVariable Long categoriaId) { return ResponseEntity.ok(ApiResponse.ok("Libros por categoría listados correctamente", service.buscarPorCategoria(categoriaId))); }
    @PostMapping
    public ResponseEntity<ApiResponse<LibroResponse>> registrar(@Valid @RequestBody LibroRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Libro registrado correctamente", service.registrar(request))); }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequest request) { return ResponseEntity.ok(ApiResponse.ok("Libro actualizado correctamente", service.actualizar(id, request))); }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.ok(ApiResponse.ok("Libro eliminado correctamente", null)); }
}
