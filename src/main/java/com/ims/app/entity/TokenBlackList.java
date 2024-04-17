package com.ims.app.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokenBlackList")
public class TokenBlackList {
    @Id
    @GeneratedValue
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String token;

    private boolean isBlackListed;

    public TokenBlackList(String token, boolean b) {
        this.token = token;
        this.isBlackListed = true;
    }
}
