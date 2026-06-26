package com.ventaslibros.interfaces.rest;

import com.ventaslibros.application.dto.common.ApiResponse;
import com.ventaslibros.application.dto.request.ClienteRequest;
import com.ventaslibros.application.dto.response.ClienteResponse;
import com.ventaslibros.application.service.ClienteService;
import com.ventaslibros.infrastructure.persistence.entity.ClienteEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> listar() { return ResponseEntity.ok(ApiResponse.ok("Clientes listados correctamente", service.listar())); }
    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> registrar(@Valid @RequestBody ClienteRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Cliente registrado correctamente", service.registrar(request))); }

    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<ClienteResponse>> getByEmail(@RequestParam String email) {
        Optional<ClienteEntity> clienteOpt = service.findByEmail(email);
        if (clienteOpt.isPresent()) {
            ClienteResponse response = service.toResponse(clienteOpt.get());
            return ResponseEntity.ok(ApiResponse.ok("Cliente encontrado", response));
        } else {
            return ResponseEntity.ok(ApiResponse.ok("Cliente no encontrado", null));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cliente actualizado correctamente", service.actualizar(id, request)));
    }
}
