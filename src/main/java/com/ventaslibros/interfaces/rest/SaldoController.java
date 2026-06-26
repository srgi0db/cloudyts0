package com.ventaslibros.interfaces.rest;

import com.ventaslibros.application.dto.common.ApiResponse;
import com.ventaslibros.application.dto.request.RecargaRequest;
import com.ventaslibros.application.dto.response.MovimientoSaldoResponse;
import com.ventaslibros.application.dto.response.RecargaSaldoResponse;
import com.ventaslibros.application.dto.response.SaldoResponse;
import com.ventaslibros.application.exception.ReglaNegocioException;
import com.ventaslibros.application.service.ClienteService;
import com.ventaslibros.application.service.SaldoService;
import com.ventaslibros.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/saldo", "/api/app/saldo"})
public class SaldoController {

    private final SaldoService saldoService;
    private final ClienteService clienteService;

    public SaldoController(SaldoService saldoService, ClienteService clienteService) {
        this.saldoService = saldoService;
        this.clienteService = clienteService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SaldoResponse>> getSaldo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long clienteId = obtenerClienteId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Saldo obtenido correctamente", saldoService.obtenerSaldoResponse(clienteId)));
    }

    @PostMapping("/recargar")
    public ResponseEntity<ApiResponse<RecargaSaldoResponse>> recargar(
            @Valid @RequestBody RecargaRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long clienteId = obtenerClienteId(userDetails);
        RecargaSaldoResponse response = saldoService.recargar(clienteId, request);
        return ResponseEntity.ok(ApiResponse.ok(response.getMensaje(), response));
    }

    @GetMapping("/recargas")
    public ResponseEntity<ApiResponse<List<MovimientoSaldoResponse>>> listarRecargas(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false, defaultValue = "RECIENTES") String ordenFecha,
            @RequestParam(required = false) String ordenMonto) {

        Long clienteId = obtenerClienteId(userDetails);
        List<MovimientoSaldoResponse> recargas = saldoService.listarRecargas(
                clienteId,
                fechaInicio,
                fechaFin,
                ordenFecha,
                ordenMonto
        );
        return ResponseEntity.ok(ApiResponse.ok("Recargas listadas correctamente", recargas));
    }

    @GetMapping("/movimientos")
    public ResponseEntity<ApiResponse<List<MovimientoSaldoResponse>>> listarMovimientos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long clienteId = obtenerClienteId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Movimientos de saldo listados correctamente", saldoService.listarMovimientos(clienteId)));
    }

    private Long obtenerClienteId(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new ReglaNegocioException("Cliente autenticado no encontrado");
        }
        return clienteService.findByEmail(userDetails.getEmail())
                .map(c -> c.getId())
                .orElseThrow(() -> new ReglaNegocioException("Cliente no encontrado"));
    }
}
