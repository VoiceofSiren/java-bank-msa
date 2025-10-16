package org.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.bank.entity.User;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserView implements Serializable {
    private String id;
    private String email;
    private String username;
    private String createdAt;

    public static UserView from(User user) {
        return UserView.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }

}
