package com.ventaslibros.interfaces.rest;

import com.ventaslibros.application.dto.common.ApiResponse;
import com.ventaslibros.application.dto.request.VentaRequest;
import com.ventaslibros.application.dto.response.PuntoRecojoResponse;
import com.ventaslibros.application.dto.response.VentaResponse;
import com.ventaslibros.application.dto.response.VentaValidacionResponse;
import com.ventaslibros.application.service.VentaService;
import com.ventaslibros.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    private final VentaService service;
    public VentaController(VentaService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VentaResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Ventas listadas correctamente", service.listar()));
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<ApiResponse<List<VentaResponse>>> misCompras(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Compras listadas correctamente", service.listarMisCompras(userDetails.getEmail())));
    }

    @GetMapping("/mis-compras/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<VentaResponse>> detalleMiCompra(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Compra encontrada", service.obtenerMiCompra(id, userDetails.getEmail())));
    }

    @GetMapping("/puntos-recojo")
    public ResponseEntity<ApiResponse<List<PuntoRecojoResponse>>> puntosRecojo() {
        return ResponseEntity.ok(ApiResponse.ok("Puntos de recojo listados correctamente", service.listarPuntosRecojo()));
    }

    @PostMapping("/validar")
    public ResponseEntity<ApiResponse<VentaValidacionResponse>> validar(
            @Valid @RequestBody VentaRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Compra validada correctamente", service.validarCompra(request, userDetails.getEmail())));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<ApiResponse<VentaResponse>> confirmar(
            @Valid @RequestBody VentaRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compra completada correctamente", service.confirmarCompraRecojo(request, userDetails.getEmail())));
    }

    @PostMapping("/confirmar-recojo")
    public ResponseEntity<ApiResponse<VentaResponse>> confirmarRecojo(
            @Valid @RequestBody VentaRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compra completada correctamente", service.confirmarCompraRecojo(request, userDetails.getEmail())));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<VentaResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Venta encontrada", service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VentaResponse>> registrar(@Valid @RequestBody VentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Venta registrada correctamente", service.registrar(request)));
    }

}
