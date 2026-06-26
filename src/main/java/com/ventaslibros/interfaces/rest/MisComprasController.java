package com.ventaslibros.interfaces.rest;

import com.ventaslibros.application.dto.common.ApiResponse;
import com.ventaslibros.application.dto.response.VentaResponse;
import com.ventaslibros.application.service.VentaService;
import com.ventaslibros.infrastructure.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mis-compras")
public class MisComprasController {

    private final VentaService ventaService;

    public MisComprasController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VentaResponse>>> listarMisCompras(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.ok("Compras listadas correctamente", ventaService.listarMisCompras(userDetails.getEmail()))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaResponse>> obtenerMiCompra(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.ok("Compra encontrada", ventaService.obtenerMiCompra(id, userDetails.getEmail()))
        );
    }

}
