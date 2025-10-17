package org.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.bank.entity.User;
import org.example.bank.entity.UserReadView;

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

    public static UserView fromReadView(UserReadView userReadView) {
        return UserView.builder()
                .id(userReadView.getId())
                .email(userReadView.getEmail())
                .username(userReadView.getUsername())
                .createdAt(userReadView.getCreatedAt().toString())
                .build();
    }
}
