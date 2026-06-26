package com.ventaslibros.interfaces.rest;

import com.ventaslibros.application.dto.common.ApiResponse;
import com.ventaslibros.application.dto.request.LoginRequest;
import com.ventaslibros.application.dto.response.LoginResponse;
import com.ventaslibros.application.service.ClienteService;
import com.ventaslibros.infrastructure.security.CustomUserDetails;
import com.ventaslibros.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    
    private final ClienteService clienteService;

    public AuthController(
    		AuthenticationManager authenticationManager,
    		UserDetailsService userDetailsService,
    		JwtService jwtService,
    		ClienteService clienteService
    ){
    	this.authenticationManager = authenticationManager;
    	this.userDetailsService = userDetailsService;
    	this.jwtService = jwtService;
    	this.clienteService = clienteService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getEmail());  
        String token = jwtService.generarToken(userDetails);
        
        String rol = "CLIENTE";
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN"))) {
            rol = "ADMIN";
        }
        
        LoginResponse loginResponse = new LoginResponse(token, "Bearer", rol);
        loginResponse.setNombres(userDetails.getNombres());
        loginResponse.setApellidos(userDetails.getApellidos());
        
        clienteService.findByEmail(request.getEmail()).ifPresent(cliente -> {
            loginResponse.setClienteId(cliente.getId().intValue());
        });
        
        return ResponseEntity.ok(ApiResponse.ok("Login correcto", loginResponse));
    }
    
    @GetMapping("/check-client")
    public ResponseEntity<ApiResponse<Boolean>> checkCliente(@RequestParam String email) {
        boolean tieneDatos = clienteService.findByEmail(email).isPresent();
        return ResponseEntity.ok(ApiResponse.ok("Consulta realizada", tieneDatos));
    }
}