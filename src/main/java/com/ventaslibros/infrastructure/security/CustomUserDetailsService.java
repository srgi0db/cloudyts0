package com.ventaslibros.infrastructure.security;

import com.ventaslibros.infrastructure.persistence.entity.UsuarioEntity;
import com.ventaslibros.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioJpaRepository repository;
    public CustomUserDetailsService(UsuarioJpaRepository repository) { this.repository = repository; }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioEntity usuario = repository.findByEmailAndEliminadoFalse(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        return new CustomUserDetails(usuario);
    }
}
