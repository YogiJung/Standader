package com.standader.hundredmilliondollars.DTO.EntranceDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginValidationReq {
    private String user_id;
    private String password;

    public LoginValidationReq(String user_id, String password) {
        this.user_id = user_id;
        this.password = password;
    }

}
