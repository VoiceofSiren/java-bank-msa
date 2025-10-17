package org.example.bank.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class TokenResponse implements Serializable {
    private String accessToken;
    private String refreshToken;
}
