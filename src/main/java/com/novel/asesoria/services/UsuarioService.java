package com.novel.asesoria.services;

import jakarta.annotation.PostConstruct;
import com.novel.asesoria.models.Tramite;
import com.novel.asesoria.models.Usuario;
import com.novel.asesoria.repositories.TramiteRepository;
import com.novel.asesoria.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final TramiteRepository tramiteRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("ESTUDIANTE");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        if ("ESTUDIANTE".equals(usuario.getRol())) {

            String[][] tramitesData = {
                {"Recolección documental inicial", "Compilación de todos los documentos personales y académicos requeridos para iniciar el proceso."},
                {"Legalización internacional", "Proceso de apostilla o legalización de documentos para su reconocimiento oficial en Argentina."},
                {"Convalidación de estudios", "Gestión administrativa para validar estudios secundarios o superiores ante las autoridades."},
                {"Planificación de traslado", "Asesoría y gestión en la compra de pasaje aéreo y contratación de seguro médico internacional."},
                {"Búsqueda de alojamiento", "Identificación y gestión de opciones habitacionales para la residencia en el país."},
                {"Preinscripción universitaria", "Registro inicial en la institución educativa, selección de sede, turno o carrera."},
                {"Ingreso al país", "Arribo al territorio argentino y presentación formal de documentos ante la universidad."},
                {"Inicio de regularización migratoria", "Apertura del expediente de residencia ante la Dirección Nacional de Migraciones."},
                {"Otorgamiento de residencia (DNI)", "Resolución favorable del trámite migratorio y emisión del documento de identidad argentino."}
            };

            // OPTIMIZACIÓN: construimos la lista completa y hacemos UN SOLO
            // insert a la base de datos con saveAll, en vez de 9 inserts separados.
            List<Tramite> tramites = new ArrayList<>();
            for (int i = 0; i < tramitesData.length; i++) {
                Tramite tramite = new Tramite();
                tramite.setNombre(tramitesData[i][0]);
                tramite.setDescripcion(tramitesData[i][1]);
                tramite.setOrden(i + 1);
                tramite.setUsuario(usuarioGuardado);
                tramite.setEstado(i == 0 ? "EN_PROCESO" : "PENDIENTE");
                tramites.add(tramite);
            }
            tramiteRepository.saveAll(tramites); // ← un solo viaje a la BD

            logger.info("Usuario estudiante '{}' creado con {} trámites", 
                        usuarioGuardado.getUsername(), tramites.size());
        }

        return usuarioGuardado;
    }

    @PostConstruct
    public void crearAdminInicial() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("superadmin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRol("ADMIN");
            admin.setNombres("Administrador");
            admin.setApellidos("Principal");
            usuarioRepository.save(admin);

            // INFO porque es un evento importante del sistema
            logger.info("Administrador inicial creado en la base de datos");
            // Nota: la contraseña del admin todavía está hardcodeada.
            // Esto lo vamos a corregir en el siguiente paso.
        }
    }
}