package com.ventaslibros.application.service;

import com.ventaslibros.application.dto.request.CambiarContrasenaRequest;
import com.ventaslibros.application.dto.request.UsuarioRequest;
import com.ventaslibros.application.dto.response.UsuarioResponse;
import com.ventaslibros.application.exception.ReglaNegocioException;
import com.ventaslibros.domain.enums.EstadoRegistro;
import com.ventaslibros.infrastructure.persistence.entity.UsuarioEntity;
import com.ventaslibros.infrastructure.persistence.repository.ClienteJpaRepository;
import com.ventaslibros.infrastructure.persistence.repository.UsuarioJpaRepository;
import com.ventaslibros.infrastructure.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioJpaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final ClienteJpaRepository clienteRepository;

    public UsuarioService(UsuarioJpaRepository usuarioRepository, PasswordEncoder passwordEncoder, ClienteJpaRepository clienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteRepository = clienteRepository;
    }

    public UsuarioResponse registrar(UsuarioRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ReglaNegocioException("El email es obligatorio");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ReglaNegocioException("La contraseña es obligatoria");
        }
        if (usuarioRepository.existsByEmailAndEliminadoFalse(request.getEmail())) {
            throw new ReglaNegocioException("El email ya está registrado");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol("CLIENTE");
        usuario.setEstado(EstadoRegistro.ACTIVO);
        usuario.setEliminado(false);

        UsuarioEntity saved = usuarioRepository.save(usuario);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAllByEliminadoFalse().stream()
                .map(this::toResponse)
                .toList();
    }

    private UsuarioResponse toResponse(UsuarioEntity u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(u.getId());
        r.setNombres(u.getNombres());
        r.setApellidos(u.getApellidos());
        r.setEmail(u.getEmail());
        r.setRol(u.getRol());
        r.setEstado(u.getEstado().name());
        return r;
    }
    
    @Transactional(readOnly = true)
    public boolean tieneDatosCliente(String email) {
        return clienteRepository.findByEmailAndEliminadoFalse(email).isPresent();
    }
    
    public void cambiarContrasena(CambiarContrasenaRequest request) {
        String email = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        UsuarioEntity usuario = usuarioRepository.findByEmailAndEliminadoFalse(email).orElseThrow(() -> new ReglaNegocioException("Usuario no encontrado"));
        if (!passwordEncoder.matches(request.getOldPassword(), usuario.getPasswordHash())) {
            throw new ReglaNegocioException("La contraseña actual es incorrecta");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);
    }
}