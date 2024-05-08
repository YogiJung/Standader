package com.standader.hundredmilliondollars.DTO.MainDTO.ChatDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomReEnterRequest {
    private List<String> message_info;
    private Boolean succeeded;
    
}
