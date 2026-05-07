package com.novel.asesoria.repositories;

import com.novel.asesoria.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByUsername(String username);
    
    // Spring crea el SQL automáticamente leyendo este nombre. Magia pura.
    Optional<Usuario> findByUsername(String username);
}