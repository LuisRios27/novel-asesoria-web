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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TramiteRepository tramiteRepository;
    private final UsuarioRepository usuarioRepository; // Añadimos el repositorio para buscar credenciales
    private final PasswordEncoder passwordEncoder;
    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        // 1. Verificamos si el nombre de usuario ya existe en la base de datos
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            // Si existe, detenemos todo y devolvemos un error 400 (Bad Request)
            return ResponseEntity.badRequest().body("Error: El nombre de usuario ya está en uso.");
        }
        
        // 2. Si no existe, procedemos a guardarlo normalmente
        Usuario nuevoUsuario = usuarioService.crearUsuario(usuario); // o usuarioRepository.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @GetMapping("/{usuarioId}/tramites")
    public List<Tramite> obtenerTramites(@PathVariable Long usuarioId) {
        return tramiteRepository.findByUsuarioIdOrderByOrdenAsc(usuarioId);
    }

    // --- EL NUEVO ENDPOINT DE LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        System.out.println("🕵️ DIAGNÓSTICO LOGIN - 1. Frontend envió: Usuario [" + username + "] y Pass [" + password + "]");

        // Buscamos al usuario SOLO por su username
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            System.out.println("❌ ERROR LOGIN - 2. No se encontró a nadie llamado '" + username + "' en la Base de Datos.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("✅ DIAGNÓSTICO LOGIN - 2. Usuario encontrado. Hash en DB: " + usuarioOpt.get().getPassword());

        // Verificamos si encajan
        boolean coincide = passwordEncoder.matches(password, usuarioOpt.get().getPassword());
        System.out.println("🕵️ DIAGNÓSTICO LOGIN - 3. ¿El texto plano encaja con el Hash?: " + coincide);

        if (coincide) {
            Usuario usuarioAutenticado = usuarioOpt.get();
            usuarioAutenticado.setPassword(null); // Borramos el hash por seguridad
            System.out.println("✅ ÉXITO - 4. Login aprobado. Devolviendo datos seguros al frontend.");
            return ResponseEntity.ok(usuarioAutenticado);
        } else {
            System.out.println("❌ ERROR LOGIN - 4. La contraseña plana NO encaja con el hash.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    // Nuevo endpoint para que el Admin vea a todos los usuarios
    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        // La magia de JPA: Al borrar el usuario, se borran sus trámites automáticamente
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}