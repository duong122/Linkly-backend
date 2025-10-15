package com.example.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret:mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpirationInMs;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return Long.parseLong(claims.getSubject());
    }
    
    public boolean validateToken(String authToken) {
        try {
            // DEBUG LOGS
            System.out.println("\n========== VALIDATING JWT TOKEN ==========");
            System.out.println("Token (first 30 chars): [" + authToken.substring(0, Math.min(30, authToken.length())) + "...]");
            System.out.println("Token length: " + authToken.length());
            System.out.println("Secret key length: " + jwtSecret.length() + " bytes");
            
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            
            System.out.println("✅ Token validation SUCCESS");
            System.out.println("==========================================\n");
            return true;
            
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            System.err.println("\n❌ JWT VALIDATION FAILED: Invalid JWT signature");
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Possible causes:");
            System.err.println("  1. Token was generated with different secret key");
            System.err.println("  2. Token was tampered with");
            System.err.println("  3. Secret key changed after token generation");
            System.err.println("==========================================\n");
        } catch (MalformedJwtException ex) {
            System.err.println("\n❌ JWT VALIDATION FAILED: Invalid JWT token format");
            System.err.println("Error: " + ex.getMessage());
            System.err.println("==========================================\n");
        } catch (ExpiredJwtException ex) {
            System.err.println("\n❌ JWT VALIDATION FAILED: Expired JWT token");
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Expired at: " + ex.getClaims().getExpiration());
            System.err.println("==========================================\n");
        } catch (UnsupportedJwtException ex) {
            System.err.println("\n❌ JWT VALIDATION FAILED: Unsupported JWT token");
            System.err.println("Error: " + ex.getMessage());
            System.err.println("==========================================\n");
        } catch (IllegalArgumentException ex) {
            System.err.println("\n❌ JWT VALIDATION FAILED: JWT claims string is empty");
            System.err.println("Error: " + ex.getMessage());
            System.err.println("==========================================\n");
        } catch (Exception ex) {
            System.err.println("\n❌ JWT VALIDATION FAILED: Unknown error");
            System.err.println("Error type: " + ex.getClass().getName());
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            System.err.println("==========================================\n");
        }
        return false;
    }
}
