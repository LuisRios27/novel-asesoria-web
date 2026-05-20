package com.novel.asesoria.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// OncePerRequestFilter garantiza que este filtro se ejecute
// exactamente una vez por cada request HTTP que llegue al servidor.
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Leemos el header "Authorization" del request
        String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza con "Bearer ", dejamos pasar
        //    el request sin autenticar (el SecurityConfig decidirá si puede continuar)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraemos el token (quitamos el prefijo "Bearer ")
        String token = authHeader.substring(7);

        // 4. Verificamos que el token sea válido
        if (jwtService.esValido(token)) {
            String username = jwtService.extraerUsername(token);
            String rol = jwtService.extraerRol(token);

            // 5. Le decimos a Spring Security quién es este usuario y qué puede hacer
            //    ROLE_ es un prefijo que Spring Security requiere por convención
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 6. Continuamos con el siguiente paso en la cadena de filtros
        filterChain.doFilter(request, response);
    }
}