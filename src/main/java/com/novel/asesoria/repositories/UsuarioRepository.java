package com.novel.asesoria.repositories;

import com.novel.asesoria.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Spring crea el SQL automáticamente leyendo este nombre. Magia pura.
    Optional<Usuario> findByUsernameAndPassword(String username, String password);
}