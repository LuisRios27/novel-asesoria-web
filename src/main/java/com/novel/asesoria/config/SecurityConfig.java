package com.novel.asesoria.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitamos CSRF porque con JWT no es necesario:
            // los tokens viajan en el header, no en cookies.
            .csrf(csrf -> csrf.disable())

            // Le decimos a Spring que NO use sesiones del servidor.
            // Cada request se autentica solo con su token, sin estado en el servidor.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Definimos qué endpoints requieren autenticación y cuáles son públicos
            .authorizeHttpRequests(auth -> auth
                // El login y los archivos estáticos son públicos
                .requestMatchers("/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/*.html", "/*.js", "/*.css", "/*.png").permitAll()

                // Solo ADMIN puede crear, eliminar usuarios y ver la lista completa
                .requestMatchers(HttpMethod.POST, "/api/usuarios").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/tramites/**").hasRole("ADMIN")

                // Cualquier otro request requiere estar autenticado
                .anyRequest().authenticated()
            )

            // Registramos nuestro filtro JWT para que se ejecute
            // antes del filtro de autenticación por defecto de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
