package com.standader.hundredmilliondollars.Controller.Entrance;


import java.util.Date;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.mysql.cj.xdevapi.Schema.Validation;
import com.standader.hundredmilliondollars.Config.JWT.*;
import com.standader.hundredmilliondollars.DTO.EntranceDTO.*;
import com.standader.hundredmilliondollars.Functions.AddCookie;
import com.standader.hundredmilliondollars.Functions.VerificationNumberGenerator;
import com.standader.hundredmilliondollars.Services.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
public class EntranceController {
    private final EntranceService entranceService;
    private JavaMailSender mailSender;

    EntranceController(EntranceService entranceService, JavaMailSender mailSender) {
        this.entranceService = entranceService;
        this.mailSender = mailSender;
    }

    // @MutationMapping
    // public LoginValidation loginValidation(@Argument(name="user_id") String user_id, @Argument(name="password") String password) {
    //     Boolean validated = entranceService.loginValidation(user_id, password);
    //     return new LoginValidation(user_id, password, validated);
    // }

    // @MutationMapping
    // public SignUpRequest signUpRequest(@Argument(name="user_id") String user_id, @Argument(name="password") String password) {
    //     Boolean succeded = entranceService.signUpRequest(user_id, password);
    //     return new SignUpRequest(user_id, password, succeded);
    // }
    @GetMapping("/entrance")
    public String Test() {
        return "This is Test";
    }
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    @PostMapping("/entrance/login")
    public ResponseEntity<LoginValidation> loginValidation(@RequestBody  LoginValidationReq request, HttpServletResponse response) {
        Boolean validated = entranceService.loginValidation(request.getUser_id(), request.getPassword());
        entranceService.loginRefreshTokenRenew(request.getUser_id(), validated, response);
        LoginValidation LoginValidationRes = new LoginValidation(request.getUser_id(), request.getPassword(), validated);
        // JWTUtil jwtUtil = new JWTUtil();
        // String cookieHeader = response.getHeader("Set-Cookie");
        // String refreshToken = null;
        // if (cookieHeader != null) {
        //     String[] cookies = cookieHeader.split(";\\s*");
        //     for (String cookie : cookies) {
        //         if (cookie.startsWith("refreshToken=")) {
        //             refreshToken = cookie.split("=", 2)[1];
        //             System.out.println("Expiration: " + jwtUtil.validateTokenExpirationRefresh(refreshToken));
        //             break;
        //         }
        //     }
        // }
        return ResponseEntity.ok(LoginValidationRes);
    }
    
    @PostMapping("/entrance/signup")
    public ResponseEntity<SignUpRequest> signUpRequest(@RequestBody SignUpRequestReq request, HttpServletResponse response) {
        Boolean succeeded = entranceService.signUpRequest(request.getUser_id(), request.getPassword());
        JWTToken jwtToken = entranceService.signUpRequestJwtRes(request.getUser_id(), succeeded, response);
        
        SignUpRequest SignUpRequestRes = new SignUpRequest(request.getUser_id(),request.getPassword(), succeeded, jwtToken);
        // String refreshToken = response.getHeader("Set-Cookie");
        // System.out.println(refreshToken);
        return ResponseEntity.ok(SignUpRequestRes);
    }

    @PostMapping("/entrance/validationtoken")
    public ResponseEntity<ValidationToken> validationToken(HttpServletRequest request, HttpServletResponse response) {
        JWTUtil jwtUtil = new JWTUtil();
        String cookieHeader = request.getHeader("Cookie");
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
        // System.out.println("controller:     " + refreshToken);
        if (jwtUtil.validateTokenAuthenticationRefresh(refreshToken) && jwtUtil.validateTokenExpirationRefresh(refreshToken)) {
            // System.out.println("/entrance/validation");
            // System.out.println("validation success");
            String user_id = jwtUtil.extractClaimsRefresh(refreshToken).getSubject();
            String refreshTokenNew = jwtUtil.generateRefreshToken(user_id);
            String accessTokenNew = jwtUtil.generateAccessToken(user_id);
            Boolean validated = true;
            jwtUtil.addRefreshTokenToCookie(refreshTokenNew, response);
            JWTToken jwtToken = new JWTToken(accessTokenNew);
            ValidationToken ValidationRes = new ValidationToken(validated, jwtToken, user_id);
            return ResponseEntity.ok(ValidationRes);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ValidationToken ValidationTokenRes = new ValidationToken(null, null, null);
            return ResponseEntity.ok(ValidationTokenRes);
        }
    }

    //나중에 SMTP 서버 이용 
    @PostMapping("/entrance/verificationemail")
    public ResponseEntity<String> verificationEmail(@RequestBody VerificationEmailReq request, HttpServletResponse response) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        try {
            String verificationNumber = VerificationNumberGenerator.generateRandomString(6);
            String sendText = "Web is being developed now Below is verification Number :                    " + verificationNumber;

            simpleMailMessage.setTo(request.getEmail());
            simpleMailMessage.setSubject("Web is being developed");
            simpleMailMessage.setText(sendText);
            mailSender.send(simpleMailMessage);
            AddCookie.addCookieToResponse(verificationNumber, response);
            return ResponseEntity.ok("Email sent successfully");
        }catch(Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send verification email.");
        }
    }
    @PostMapping("/entrance/verificationcheck")
    public ResponseEntity<String> verificationCheck(@RequestBody VerificationCheckReq request, @CookieValue("verificationNumber") String verificationNumberCookie ) {
        if (verificationNumberCookie != null) {
            if (request.getVerificationNumber().equals(verificationNumberCookie)) {
                return ResponseEntity.ok("Ok");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify verification number.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No verification number provided.");
        }
        
    }

}
