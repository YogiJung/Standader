package com.standader.hundredmilliondollars.DTO.EntranceDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import  com.standader.hundredmilliondollars.Config.JWT.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationToken {
    private Boolean validated;
    private JWTToken jwtToken;
    private String user_id;

}