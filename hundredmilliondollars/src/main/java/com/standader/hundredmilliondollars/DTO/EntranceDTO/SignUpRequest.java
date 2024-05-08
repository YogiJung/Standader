package com.standader.hundredmilliondollars.DTO.EntranceDTO;

import com.standader.hundredmilliondollars.Config.JWT.JWTToken;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    private String user_id;
    private String password;
    private Boolean succeeded;
    private JWTToken jwtToken;

    public SignUpRequest(String user_id, String password, Boolean succeeded, JWTToken jwtToken) {
        this.user_id = user_id;
        this.password = password;
        this.succeeded = succeeded;
        this.jwtToken = jwtToken;
    }

}
