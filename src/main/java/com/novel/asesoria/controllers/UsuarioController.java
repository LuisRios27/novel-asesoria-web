package com.novel.asesoria.controllers;

import com.novel.asesoria.models.Tramite;
import com.novel.asesoria.models.Usuario;
import com.novel.asesoria.repositories.TramiteRepository;
import com.novel.asesoria.repositories.UsuarioRepository;
import com.novel.asesoria.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    // Así se crea un logger profesional en Java.
    // Le pasamos la clase actual para que en los logs sepas exactamente
    // desde qué archivo vino cada mensaje.
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;
    private final TramiteRepository tramiteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            return ResponseEntity.badRequest().body("Error: El nombre de usuario ya está en uso.");
        }
        Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @GetMapping("/{usuarioId}/tramites")
    public List<Tramite> obtenerTramites(@PathVariable Long usuarioId) {
        return tramiteRepository.findByUsuarioIdOrderByOrdenAsc(usuarioId);
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        // DEBUG: solo aparece en desarrollo, nunca en producción.
        // Nota: NUNCA logueamos la contraseña, ni siquiera en DEBUG.
        logger.debug("Intento de login para el usuario: {}", username);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            // WARN porque es algo que vale la pena monitorear (intentos fallidos)
            logger.warn("Intento de login fallido: usuario '{}' no encontrado", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean coincide = passwordEncoder.matches(password, usuarioOpt.get().getPassword());

        if (coincide) {
            Usuario usuarioAutenticado = usuarioOpt.get();
            usuarioAutenticado.setPassword(null);
            logger.info("Login exitoso para el usuario: {}", username);
            return ResponseEntity.ok(usuarioAutenticado);
        } else {
            logger.warn("Intento de login fallido: contraseña incorrecta para '{}'", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}