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


@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TramiteRepository tramiteRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        // 1. Asignar rol por defecto
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("ESTUDIANTE");
        }
        // Tomamos la contraseña en texto plano, la pasamos por BCrypt, y la volvemos a guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // 2. Guardar al usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        if ("ESTUDIANTE".equals(usuario.getRol())) {
            
            // Matriz con [Nombre del trámite, Descripción]
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

            for (int i = 0; i < tramitesData.length; i++) {
                Tramite tramite = new Tramite();
                tramite.setNombre(tramitesData[i][0]);
                tramite.setDescripcion(tramitesData[i][1]);
                tramite.setOrden(i + 1);
                tramite.setUsuario(usuarioGuardado);

                // El primero empieza en proceso, los demás pendientes
                if (i == 0) {
                    tramite.setEstado("EN_PROCESO");
                } else {
                    tramite.setEstado("PENDIENTE");
                }
                
                tramiteRepository.save(tramite);
            }
        }

        return usuarioGuardado;
    }

    @PostConstruct
    public void crearAdminInicial() {
        // Solo creamos el admin si la base de datos está completamente vacía
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("superadmin"); // Tu usuario de acceso
            // Hasheamos la contraseña del admin en lugar de guardarla como texto plano
            admin.setPassword(passwordEncoder.encode("123456"));
            
            // --- LÍNEA DE DIAGNÓSTICO (NUEVA) ---
            System.out.println("🕵️ DIAGNÓSTICO - Hash generado en memoria: " + admin.getPassword());
            // -----------------------------------
            
            admin.setRol("ADMIN");
            admin.setNombres("Administrador");
            admin.setApellidos("Principal");
            // Si tu entidad Usuario requiere otros campos obligatorios, agrégalos aquí
            
            usuarioRepository.save(admin);
            System.out.println("✅ Administrador inicial creado con éxito en la base de datos.");
        }
    }
}