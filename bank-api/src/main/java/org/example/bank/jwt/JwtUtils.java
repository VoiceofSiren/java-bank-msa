package org.example.bank.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    private final SecretKey secretKey;

    public JwtUtils(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Claims extractPayload(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("JWT token is null or empty");
        }

        try {
            return Jwts
                    // secretKey를 이용하여 파싱
                    .parser().verifyWith(secretKey)
                    // token 값을 이용
                    .build().parseSignedClaims(token)
                    // payload 추출
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 클레임 수동 추출
            return e.getClaims();
        }
    }

    public String getUserId(String token) {
        return extractPayload(token).get("userId", String.class);
    }

    public String getUsername(String token) {
        return extractPayload(token).get("username", String.class);
    }

    public String getEmail(String token) {
        return extractPayload(token).get("email", String.class);
    }

    public Boolean expires(String token) {
        return extractPayload(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String issueAccessToken(String userId, String username, String email, Long expirationMillis) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(secretKey)
                .compact();
    }

    public String issueRefreshToken(String userId, String username, String email, Long expirationMillis) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(secretKey)
                .compact();
    }
}