package org.example.bank.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue()
                .set(email, refreshToken, JwtConstants.REFRESH_TOKEN_EXPIRATION);
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue()
                .get(email);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }

}
