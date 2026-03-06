package com.novel.asesoria.repositories;

import com.novel.asesoria.models.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TramiteRepository extends JpaRepository<Tramite, Long> {
    
    // Spring crea la consulta SQL automáticamente al leer este nombre:
    List<Tramite> findByUsuarioIdOrderByOrdenAsc(Long usuarioId);
}