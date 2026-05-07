package com.novel.asesoria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Este es el motor que transformará las contraseñas en huellas digitales
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Por ahora, le decimos a Spring que permita entrar a todo 
    // para que podamos seguir probando mientras refactorizamos
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para pruebas de API
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permitimos todo temporalmente
            );
        return http.build();
    }
}
