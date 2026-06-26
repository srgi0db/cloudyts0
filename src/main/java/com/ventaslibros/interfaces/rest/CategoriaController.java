package com.ventaslibros.interfaces.rest;

import com.ventaslibros.application.dto.common.ApiResponse;
import com.ventaslibros.application.dto.response.CategoriaResponse;
import com.ventaslibros.application.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaService service;
    public CategoriaController(CategoriaService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> listar() { return ResponseEntity.ok(ApiResponse.ok("Categorías listadas correctamente", service.listar())); }
}
