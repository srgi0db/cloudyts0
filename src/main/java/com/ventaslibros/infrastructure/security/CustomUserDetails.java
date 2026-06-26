package com.ventaslibros.infrastructure.security;

import com.ventaslibros.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final UsuarioEntity usuario;

    public CustomUserDetails(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public String getNombres() {
        return usuario.getNombres();
    }

    public String getApellidos() {
        return usuario.getApellidos();
    }

    public String getEmail() {
        return usuario.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
    }

    @Override
    public String getPassword() {
        return usuario.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return usuario.getEstado() == com.ventaslibros.domain.enums.EstadoRegistro.ACTIVO;
    }
}