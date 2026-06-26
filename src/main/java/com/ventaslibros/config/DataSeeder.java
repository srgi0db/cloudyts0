package com.ventaslibros.config;

import com.ventaslibros.domain.enums.EstadoRegistro;
import com.ventaslibros.infrastructure.persistence.entity.CategoriaEntity;
import com.ventaslibros.infrastructure.persistence.entity.UsuarioEntity;
import com.ventaslibros.infrastructure.persistence.repository.CategoriaJpaRepository;
import com.ventaslibros.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(UsuarioJpaRepository usuarios, CategoriaJpaRepository categorias, PasswordEncoder encoder) {
        return args -> {
            if (!usuarios.existsByEmailAndEliminadoFalse("admin@ventaslibros.com")) {
                UsuarioEntity admin = new UsuarioEntity();
                admin.setNombres("Administrador"); admin.setApellidos("Sistema"); admin.setEmail("admin@ventaslibros.com");
                admin.setPasswordHash(encoder.encode("Admin12345")); admin.setRol("ADMIN"); admin.setEstado(EstadoRegistro.ACTIVO); admin.setEliminado(false);
                usuarios.save(admin);
            }
            if (categorias.count() == 0) {
                for (String nombre : List.of("Programación", "Base de datos", "Arquitectura de software", "Negocios", "Literatura")) {
                    CategoriaEntity c = new CategoriaEntity(); c.setNombre(nombre); c.setEstado(EstadoRegistro.ACTIVO); c.setEliminado(false); categorias.save(c);
                }
            }
        };
    }
}
