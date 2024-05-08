package com.standader.hundredmilliondollars.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="chat_room_user_mapping")
public class ChatRoomUserMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idChatRoomUserMapping;

    @ManyToOne
    @JoinColumn(name = "idUser", referencedColumnName = "idUser")
    private User user;

    @JoinColumn(name = "idChatRoom", referencedColumnName = "idChatRoom")
    @ManyToOne
    private ChatRoom chatRoom;

    public ChatRoomUserMapping(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
    }
}
