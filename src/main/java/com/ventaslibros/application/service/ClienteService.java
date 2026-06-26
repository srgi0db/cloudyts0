package com.ventaslibros.application.service;

import com.ventaslibros.application.dto.request.ClienteRequest;
import com.ventaslibros.application.dto.response.ClienteResponse;
import com.ventaslibros.application.exception.RecursoNoEncontradoException;
import com.ventaslibros.application.exception.ReglaNegocioException;
import com.ventaslibros.domain.enums.EstadoRegistro;
import com.ventaslibros.infrastructure.persistence.entity.ClienteEntity;
import com.ventaslibros.infrastructure.persistence.repository.ClienteJpaRepository;
import com.ventaslibros.infrastructure.persistence.repository.UsuarioJpaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {
    private final ClienteJpaRepository clienteRepository;
    private final UsuarioJpaRepository usuarioRepository;
    
    public ClienteService(ClienteJpaRepository clienteRepository, UsuarioJpaRepository usuarioRepository) {
    	this.clienteRepository = clienteRepository;
    	this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() { return clienteRepository.findAllByEliminadoFalseOrderByApellidosAscNombresAsc().stream().map(this::toResponse).toList(); }

    public ClienteResponse registrar(ClienteRequest request) {
        if (clienteRepository.existsByNumeroDocumentoAndEliminadoFalse(request.getNumeroDocumento())) throw new ReglaNegocioException("El documento ya está registrado");
        ClienteEntity c = new ClienteEntity();
        aplicar(c, request);
        c.setEstado(EstadoRegistro.ACTIVO);
        c.setEliminado(false);
        return toResponse(clienteRepository.save(c));
    }

    ClienteEntity obtenerCliente(Long id) { return clienteRepository.findByIdAndEliminadoFalse(id).orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado")); }

    private void aplicar(ClienteEntity c, ClienteRequest r) {
        c.setNombres(r.getNombres()); c.setApellidos(r.getApellidos()); c.setTipoDocumento(r.getTipoDocumento());
        c.setNumeroDocumento(r.getNumeroDocumento()); c.setEmail(r.getEmail()); c.setTelefono(r.getTelefono());
    }

    public ClienteResponse toResponse(ClienteEntity c) {
        ClienteResponse r = new ClienteResponse();
        r.setId(c.getId()); r.setNombres(c.getNombres()); r.setApellidos(c.getApellidos()); r.setTipoDocumento(c.getTipoDocumento());
        r.setNumeroDocumento(c.getNumeroDocumento()); r.setEmail(c.getEmail()); r.setTelefono(c.getTelefono()); r.setEstado(c.getEstado().name());
        return r;
    }
    
    public Optional<ClienteEntity> findByEmail(String email) {
        return clienteRepository.findByEmailAndEliminadoFalse(email);
    }
    
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        ClienteEntity cliente = obtenerCliente(id);
        if (!cliente.getNumeroDocumento().equals(request.getNumeroDocumento()) &&
                clienteRepository.existsByNumeroDocumentoAndEliminadoFalse(request.getNumeroDocumento())) {
                throw new ReglaNegocioException("El número de documento ya está registrado");
        }
        
        aplicar(cliente, request);
        cliente = clienteRepository.save(cliente);
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            usuarioRepository.findByEmailAndEliminadoFalse(cliente.getEmail())
                .ifPresent(usuario -> {
                    usuario.setEmail(request.getEmail());
                    usuario.setNombres(request.getNombres());
                    usuario.setApellidos(request.getApellidos());
                    usuarioRepository.save(usuario);
                });
        }
        return toResponse(cliente);
    }
}
