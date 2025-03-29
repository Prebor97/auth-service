package com.prebs.auth_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMs;

    public String createJwt(String id, Set<String> roles, String email){
        System.out.println("Generating JWT...................................................");
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .subject(email)
                .claim("user_id", id)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validityInMs))
                .signWith(key)
                .compact();
    }
}
