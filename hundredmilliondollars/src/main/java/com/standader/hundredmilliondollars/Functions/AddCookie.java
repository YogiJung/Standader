package com.standader.hundredmilliondollars.Functions;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AddCookie {
    public static void addCookieToResponse(String verificationNumber , HttpServletResponse response) {
        Cookie verificationCookie = new Cookie("verificationNumber", verificationNumber);

        // System.out.println("Here is refresh" + refreshToken);
        verificationCookie.setHttpOnly(true);
        verificationCookie.setSecure(true);
        verificationCookie.setAttribute("SameSite", "None"); 
        verificationCookie.setDomain("localhost"); 
        verificationCookie.setPath("/"); 
        verificationCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(verificationCookie);
    }
}
