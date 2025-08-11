package com.pragma.powerup.infrastructure.configuration.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

@Component
public class JwtUtils {

    private final String SECRET_KEY;

    public JwtUtils(@Value("${application.security.jwt.secret-key}") String secretKey) {
        this.SECRET_KEY = secretKey;
    }

    private byte[] getSigningKey() {
        return Base64.getDecoder().decode(SECRET_KEY);
    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public String extractUserId(String token){
        return extractClaim(token,Claims::getId);
    }

    public List<String> extractRoles(String token){
        Claims claims = extractAllClaims(token);
        Object rolesObj = claims.get("roles");
        if(rolesObj instanceof List){
            List<?> rolesList = (List<?>) rolesObj;
            return rolesList.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token){
        try{
            extractAllClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String generateTestToken(int id, String role) {
        return Jwts.builder()
                .setSubject("user_de_prueba")
                .setId(""+id)
                .claim("roles", List.of(role))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, hmacShaKeyFor(getSigningKey()))
                .compact();
    }


}
