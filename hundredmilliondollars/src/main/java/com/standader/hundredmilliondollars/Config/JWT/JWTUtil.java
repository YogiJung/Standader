package com.standader.hundredmilliondollars.Config.JWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class JWTUtil {
    private static final byte[] secretKey = new byte[32];
    private static final byte[] secretKeyRefresh = new byte[32];
    public static String generateAccessToken(String user_id) {
        return Jwts.builder()
            .subject(user_id)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 10))
            .signWith(Keys.hmacShaKeyFor(secretKey))
            .compact();
    }
    public static String generateRefreshToken(String user_id) {
        return Jwts.builder()
            .subject(user_id)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 1000)) // Interger Overflow방지 --> Long Type 사용
            .signWith(Keys.hmacShaKeyFor(secretKeyRefresh))
            .compact();
    }
    public Claims extractClaims(String token) {
        try {
            Claims claim = Jwts.parser()
                            .verifyWith(Keys.hmacShaKeyFor(secretKey))
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
                            return claim;

        } catch(Exception e) {
            System.err.println("Token is null or empty or not processed");
            return null;
        }
    }
    public Claims extractClaimsRefresh(String refreshToken) {

        try {
            Claims claim = Jwts.parser()
                            .verifyWith(Keys.hmacShaKeyFor(secretKeyRefresh))
                            .build()
                            .parseSignedClaims(refreshToken)
                            .getPayload();
            return claim;
            
        } catch (Exception e) {
            System.err.println("Token is null or empty or not processed");
            return null;
        }
        
    }

    public Boolean validateTokenExpiration(String token) {
        try {
            Claims claim = extractClaims(token);
            Date expirationDate = claim.getExpiration();
            if (expirationDate.after(new Date())) {
                // System.out.println("expiration success");
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Token is null or empty or not processed");
            return false;
        }
    }
    public Boolean validateTokenExpirationRefresh(String refreshToken) {

        try {
            Claims claim = extractClaimsRefresh(refreshToken);
            Date expirationDate = claim.getExpiration();
            System.out.println("expirationDate:    " + expirationDate);
            if (expirationDate.after(new Date())) {
                // System.out.println("expiration success");
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Token is null or empty or not processed");
            return false;
        }
    }

    public Boolean validateTokenAuthentication(String token) {

        try {
            Claims claim = extractClaims(token);
            Date issueDate = claim.getIssuedAt();
        if (issueDate == null || issueDate.after(new Date())) {
            // System.out.println("issue date false");
            return false;
        }
        // System.out.println("authenticate true");
        return true;
        } catch(JwtException e) {
            return false;
        }
    }
    public Boolean validateTokenAuthenticationRefresh(String refreshToken) {

        try {
            Claims claim = extractClaimsRefresh(refreshToken);
            Date issueDate = claim.getIssuedAt();
        if (issueDate == null || issueDate.after(new Date())) {
            // System.out.println("issue date false");
            return false;
        }
        // System.out.println("authenticate true");
        return true;
        } catch(JwtException e) {
            return false;
        }
    }
    public static void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        // System.out.println("Here is refresh" + refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setAttribute("SameSite", "None"); 
        refreshTokenCookie.setDomain("localhost"); 
        refreshTokenCookie.setPath("/"); 
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);
    }

}
