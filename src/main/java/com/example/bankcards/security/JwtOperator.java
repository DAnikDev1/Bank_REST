package com.example.bankcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtOperator {
    @Value("${bank.security.jwt_secret_key}")
    private String jwtSecretKey;

    @Value("${bank.security.jwt_life_time}")
    private int jwtLifeTime;

    public String generateToken(UserDetails userDetails) {
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;

        return Jwts.builder()
                .claim("authorities", userPrincipal.getAuthoritiesStrings())
                .setSubject(String.valueOf(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtLifeTime))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public Long extractUserId(String token) {
        String id = extractClaim(token, Claims::getSubject);
        return Long.parseLong(id);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        UserPrincipal principal = (UserPrincipal) userDetails;
        final Long tokenUserId = extractUserId(token);
        return (tokenUserId.equals(principal.getId()) && !isTokenExpired(token));
    }
}
