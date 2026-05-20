package com.novel.asesoria.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Este componente es el "experto en JWT" de tu aplicación.
// Sabe crear tokens, leerlos y verificar si son válidos.
@Component
public class JwtService {

    // Lee el secreto y la expiración desde application-dev.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Construye la clave criptográfica a partir del texto secreto
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Crea un nuevo token para un usuario dado
    public String generarToken(String username, String rol) {
        return Jwts.builder()
                .subject(username)          // quién es el usuario
                .claim("rol", rol)          // su rol (ADMIN o ESTUDIANTE)
                .issuedAt(new Date())       // cuándo se creó
                .expiration(new Date(System.currentTimeMillis() + expiration)) // cuándo expira
                .signWith(getSigningKey())  // lo firmamos con nuestra clave secreta
                .compact();
    }

    // Extrae el username del token (si el token es válido)
    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    // Extrae el rol del token
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    // Verifica si el token es válido y no expiró
    public boolean esValido(String token) {
        try {
            Claims claims = extraerClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // Si el token está mal formado, fue modificado, o expiró,
            // jjwt lanza una excepción. La capturamos y devolvemos false.
            return false;
        }
    }

    // Lee y verifica la firma del token, devolviendo su contenido
    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}