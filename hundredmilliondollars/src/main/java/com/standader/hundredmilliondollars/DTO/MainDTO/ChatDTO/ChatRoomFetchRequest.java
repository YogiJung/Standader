package com.standader.hundredmilliondollars.DTO.MainDTO.ChatDTO;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomFetchRequest {
    private Boolean succeeded;
    private List<String> participated_chat_rooms;

    public ChatRoomFetchRequest(Boolean succeeded, List<String> participated_chat_rooms) {
        this.succeeded = succeeded;
        this.participated_chat_rooms = participated_chat_rooms;
    }
}
