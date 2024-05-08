package com.standader.hundredmilliondollars.DTO.EntranceDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginValidation {
    private String user_id;
    private String password;
    private Boolean validated;

    public LoginValidation(String user_id, String password, Boolean validated) {
        this.user_id = user_id;
        this.password = password;
        this.validated = validated;
    }
}
