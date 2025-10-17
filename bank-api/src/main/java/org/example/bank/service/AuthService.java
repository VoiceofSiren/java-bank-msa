package org.example.bank.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.example.bank.jwt.JwtConstants;
import org.example.bank.jwt.JwtUtils;
import org.example.bank.jwt.RefreshTokenService;
import org.example.bank.jwt.TokenResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public TokenResponse reissueTokens(String oldRefreshToken) {
        // [1] Refresh Token 유효성 검증
        if (!jwtUtils.validateToken(oldRefreshToken)) {
            throw new JwtException("Invalid refresh token: " + oldRefreshToken);
        }

        // [2] 사용자 정보 추출
        String email = jwtUtils.getEmail(oldRefreshToken);

        // [3] Redis에서 저장된 Refresh Token 조회
        String savedToken = refreshTokenService.getRefreshToken(email);
        if (savedToken == null || !savedToken.equals(oldRefreshToken)) {
            throw new JwtException("Invalid refresh token: " + oldRefreshToken);
        }

        // 4. 새로운 Access Token 및 Refresh Token 발급
        String userId = jwtUtils.getUserId(oldRefreshToken);
        String username = jwtUtils.getUsername(oldRefreshToken);
        String newAccessToken = jwtUtils.issueAccessToken(userId, username, email, JwtConstants.ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtUtils.issueRefreshToken(userId, username, email, JwtConstants.REFRESH_TOKEN_EXPIRATION);

        // [5] Redis에 새로운 Refresh Token 갱신 (DB나 Redis에 저장)
        refreshTokenService.saveRefreshToken(email, newRefreshToken);

        // 6. 클라이언트로 반환
        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}