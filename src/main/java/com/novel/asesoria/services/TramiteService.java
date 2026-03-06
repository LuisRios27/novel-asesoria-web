package com.novel.asesoria.services;

import com.novel.asesoria.models.Tramite;
import com.novel.asesoria.repositories.TramiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TramiteService {

    private final TramiteRepository tramiteRepository;

    @Transactional
    public void avanzarTramite(Long tramiteId) {
        // 1. Buscamos el trámite al que el admin le hizo clic
        Tramite tramiteActual = tramiteRepository.findById(tramiteId)
                .orElseThrow(() -> new RuntimeException("Trámite no encontrado"));

        // 2. Lo ponemos en verde (FINALIZADO)
        tramiteActual.setEstado("FINALIZADO");
        tramiteRepository.save(tramiteActual);

        // 3. Buscamos todos los trámites de ese estudiante
        List<Tramite> tramitesDelUsuario = tramiteRepository.findByUsuarioIdOrderByOrdenAsc(tramiteActual.getUsuario().getId());
        
        // 4. Buscamos el que sigue (orden + 1) y lo ponemos amarillo (EN_PROCESO)
        for (Tramite t : tramitesDelUsuario) {
            if (t.getOrden() == tramiteActual.getOrden() + 1) {
                t.setEstado("EN_PROCESO");
                tramiteRepository.save(t);
                break; // Ya encontramos el siguiente, dejamos de buscar
            }
        }
    }
    
    @Transactional
    public void reiniciarTramites(Long usuarioId) {
        List<Tramite> tramites = tramiteRepository.findByUsuarioIdOrderByOrdenAsc(usuarioId);

        for (Tramite t : tramites) {
            if (t.getOrden() == 1) {
                t.setEstado("EN_PROCESO");
            } else {
                t.setEstado("PENDIENTE");
            }
            tramiteRepository.save(t);
        }
    }
}