package com.novel.asesoria.controllers;

import com.novel.asesoria.services.TramiteService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tramites")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TramiteController {

    private final TramiteService tramiteService;

    // Cuando el admin haga clic en la bolita verde, este código hace el efecto dominó
    @PutMapping("/{tramiteId}/avanzar")
    public String avanzarTramite(@PathVariable Long tramiteId) {
        tramiteService.avanzarTramite(tramiteId);
        return "¡Trámite avanzado al siguiente paso con éxito!";
    }

    @PutMapping("/usuario/{usuarioId}/reiniciar")
    public ResponseEntity<Void> reiniciarTramites(@PathVariable Long usuarioId) {
        tramiteService.reiniciarTramites(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
