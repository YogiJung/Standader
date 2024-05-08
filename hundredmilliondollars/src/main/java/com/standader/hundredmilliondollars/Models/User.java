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
@NoArgsConstructor
@Setter
@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;
    @Column(name="userId")
    private String userId;
    @Column(name="password")
    private String password;
    @OneToMany(mappedBy = "user")
    private List<ChatRoomUserMapping> chatRoomUserMapping;
    

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}


