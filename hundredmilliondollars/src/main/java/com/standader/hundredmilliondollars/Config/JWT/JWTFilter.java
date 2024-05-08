package com.standader.hundredmilliondollars.Config.JWT;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {
    private JWTUtil jwtUtil = new JWTUtil();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException{
        final String authroziationHeader = request.getHeader("Authorization");
        final String cookieHeader = request.getHeader("Cookie");
        String user_id = null;
        String accessToken = null;
        String refreshToken = null;
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";\\s*");
            for (String cookie : cookies) {
                if (cookie.startsWith("refreshToken=")) {
                    refreshToken = cookie.split("=", 2)[1];
                    break;
                }
            }
        }
        
        // System.out.println(refreshToken);
        // System.out.println(jwtUtil.extractClaims(refreshToken));
        if(authroziationHeader != null && authroziationHeader.startsWith("Bearer ")) {
            accessToken = authroziationHeader.substring(7);
            Boolean accessTokenValid = true;
            if (accessToken != null) {
                try {
                    user_id = jwtUtil.extractClaims(accessToken).getSubject();
                } catch(ExpiredJwtException e) {
                    System.out.println("access token expired");
                    accessTokenValid = false;
                }
            }
            if (!accessTokenValid && refreshToken != null) {
                try {
                    user_id = jwtUtil.extractClaims(refreshToken).getSubject();
                }catch(ExpiredJwtException e) {
                    System.out.println("refresh token expired");
                    accessTokenValid = false;
                }
            }
            // System.out.println(accessToken);
        if ((jwtUtil.validateTokenAuthentication(accessToken) && jwtUtil.validateTokenExpiration(accessToken)) || (jwtUtil.validateTokenAuthenticationRefresh(refreshToken) && jwtUtil.validateTokenExpirationRefresh(refreshToken))) {
            if (user_id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user_id, null, AuthorityUtils.NO_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        }

        

        try {
            chain.doFilter(request, response);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
