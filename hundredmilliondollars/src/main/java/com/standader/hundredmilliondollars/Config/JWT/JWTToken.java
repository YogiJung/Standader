package com.standader.hundredmilliondollars.Config.JWT;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JWTToken {
    private String accessToken;

    public JWTToken(String accessToken) {
        this.accessToken = accessToken;
        
    }
}
