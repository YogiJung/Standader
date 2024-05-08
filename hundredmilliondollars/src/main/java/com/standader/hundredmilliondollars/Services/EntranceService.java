package com.standader.hundredmilliondollars.Services;
import com.standader.hundredmilliondollars.Repository.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.standader.hundredmilliondollars.Config.JWT.JWTToken;
import com.standader.hundredmilliondollars.Config.JWT.JWTUtil;
import com.standader.hundredmilliondollars.Models.*;
// Models, Repository = Database layer
//Service = Server (Business Logic) Layer
// Controller = Client Layer


@Service
public class EntranceService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EntranceService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Boolean loginValidation(String user_id, String password) {
        Optional<User> user = userRepository.findByUserId(user_id);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    public void loginRefreshTokenRenew(String user_id ,Boolean validated, HttpServletResponse response) {
        if (validated) {
            JWTUtil.addRefreshTokenToCookie(JWTUtil.generateRefreshToken(user_id), response);
            System.out.println("Login Validation Renew in validation");
        }
        
        System.out.println("Login Validation Renew");
    }

    public Boolean signUpRequest(String user_id, String password) {
        
        User user = new User(user_id, password);
        try {
            userRepository.save(user);
            return true;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public JWTToken signUpRequestJwtRes(String user_id ,Boolean succeeded, HttpServletResponse response) {
        if (succeeded) {
            JWTToken jwtToken = new JWTToken(JWTUtil.generateAccessToken(user_id));
            // System.out.println(user_id);
            JWTUtil.addRefreshTokenToCookie(JWTUtil.generateRefreshToken(user_id), response);
            return jwtToken;
        } else {
            JWTToken jwtToken = null;
            return jwtToken;
        }
    }
}
