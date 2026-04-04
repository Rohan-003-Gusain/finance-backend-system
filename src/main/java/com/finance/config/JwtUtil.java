package com.finance.config;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.finance.model.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration}")
	private long tokenValidity;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}
    
    // ========== GENERATE JWT TOKEN ==========
    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256) 
                .compact();
    }

    // ========== EXTRACT USERNAME FROM TOKEN ==========
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ========== EXTRACT CLAIM FROM TOKEN ==========
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    // ========== EXTRACT ROLE FROM TOKEN ==========
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // ========== PARSE ALL CLAIM ==========
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    // ========== VALIDATE JWT TOKEN ==========
    public boolean validateToken(String token, UserDetails userDetails) {
    	final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    
    // ========== CHECK TOKEN EXPIRATION ==========
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
