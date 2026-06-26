package com.ventaslibros.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    @Value("${app.jwt.secret}")
    private String secret;
    @Value("${app.jwt.expiration-ms}")
    private Long expirationMs;

    public String generarToken(UserDetails userDetails) {
        Date now = new Date();
        return Jwts.builder().subject(userDetails.getUsername()).issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs)).signWith(getKey()).compact();
    }
    public String obtenerEmail(String token) { return claims(token).getSubject(); }
    public boolean esValido(String token, UserDetails userDetails) { return obtenerEmail(token).equals(userDetails.getUsername()) && !claims(token).getExpiration().before(new Date()); }
    private Claims claims(String token) { return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload(); }
    private SecretKey getKey() { return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
}
