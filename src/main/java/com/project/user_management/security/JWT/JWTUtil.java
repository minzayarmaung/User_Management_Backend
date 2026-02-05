package com.project.user_management.security.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component

public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    public long ACCESS_TOKEN_VALID_TIME_MILLIS() {
        long validMinutes = 3; // 3 minutes
        return validMinutes * 60 * 1000L;
    }

    public long REFRESH_TOKEN_VALID_TIME_MILLIS() {
        long validHour = 24 * 7; // 7 days for refresh
        return validHour * 60 * 60 * 1000L;
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

//    private Key getSigningKey() {
//        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    }

    public String extractEmail(String token) throws ExpiredJwtException, JwtException{
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Adding Custom Claims ( Testing Yet :3)
        String email = claims.get("email" , String.class);
        return (email != null) ? email : claims.getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractEmail(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractTokenFromRequest(HttpServletRequest request){
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Fall back, just in case old bear flow must work too :")
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }
        return null;
    }

    // Important Code to Check later =_=
    public String generateToken(final String email){
        return generateToken(email , ACCESS_TOKEN_VALID_TIME_MILLIS());
    }

    public String generateRefreshToken(final String email){
        return generateToken(email , REFRESH_TOKEN_VALID_TIME_MILLIS());
    }

    public String generateToken(final String email ,final long expirationTime){
        return Jwts.builder()
                .setSubject(email)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(final String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}

































