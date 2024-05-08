package com.standader.hundredmilliondollars.Models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idChatRoom;
    @Column(name = "roomId")
    private String roomId;
    @OneToMany(mappedBy = "chatRoom")
    private List<ChatRoomUserMapping> chatRoomUserMapping;



    public ChatRoom(String roomId) {
        this.roomId = roomId;
    }
}
