package com.novel.asesoria.services;

import com.novel.asesoria.models.Tramite;
import com.novel.asesoria.models.Usuario;
import com.novel.asesoria.repositories.TramiteRepository;
import com.novel.asesoria.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TramiteRepository tramiteRepository;

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        // 1. Asignar rol por defecto
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("ESTUDIANTE");
        }
        
        // 2. Guardar al usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 3. ¡LA VERDADERA LÓGICA DE NÓVEL ASESORÍA!
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
}