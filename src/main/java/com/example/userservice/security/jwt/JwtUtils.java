package com.example.userservice.security.jwt;

import com.example.userservice.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    // Generate a secure random key suitable for HS256
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // Generates a JWT token containing username as subject
    public String generateToken(User user) {
        return Jwts
                .builder()
                .claim("email", user.getEmail())
                .claim("uid", String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour validity
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Validates the token and returns true if it is valid
    public boolean validateToken(String token) {
        try {
            // Parse the token and retrieve claims
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Check if the token is expired
            Date expirationDate = claims.getExpiration();
            if (expirationDate != null && expirationDate.before(new Date())) {
                throw new Exception("Token expired please login to continue");
            }

            return true;  // Token is valid and not expired
        } catch (Exception e) {
            System.out.println(e + "error123");
            if (e.getMessage().startsWith("JWT expired")) {
                System.out.println("INSIDE IF BLOCK");
            }
            return false;
        }
    }

    // Extracts the uid from the token
    public String getUidFromToken(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("uid", String.class);
    }
}
