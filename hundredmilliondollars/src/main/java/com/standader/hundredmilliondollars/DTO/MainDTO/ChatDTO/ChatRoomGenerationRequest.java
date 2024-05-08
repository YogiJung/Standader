package com.standader.hundredmilliondollars.DTO.MainDTO.ChatDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomGenerationRequest {
    private Boolean succeeded;

    public ChatRoomGenerationRequest(Boolean succeeded) {
        this.succeeded = succeeded;
    }
}
