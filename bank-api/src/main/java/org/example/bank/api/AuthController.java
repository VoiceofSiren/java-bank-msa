package org.example.bank.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.bank.common.ApiResponse;
import org.example.bank.jwt.*;
import org.example.bank.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        String email = jwtUtils.getEmail(refreshToken);
        if (refreshTokenService.getRefreshToken(email) != null) {
            refreshTokenService.deleteRefreshToken(email);
        }

        // 쿠키 삭제
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }


    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        String email = jwtUtils.getEmail(refreshToken);
        if (refreshTokenService.getRefreshToken(email) != null) {
            refreshTokenService.deleteRefreshToken(email);
        }

        String userId = jwtUtils.getUserId(refreshToken);
        String username = jwtUtils.getUsername(refreshToken);

        String newAccessToken = jwtUtils.issueAccessToken(userId, username, email, JwtConstants.ACCESS_TOKEN_EXPIRATION);

        // 새 Access Token을 헤더로 응답
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        return ResponseEntity.ok("Access Token reissued successfully");
    }

    /**
     *  RTR 기법
     * @param refreshToken
     * @param response
     * @return
     */
    @Operation(summary = "Reissue access and refresh token", description = "Reissue both access and refresh tokens when access token expires")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshTokens(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response) {

        TokenResponse newTokens = authService.reissueTokens(refreshToken);
        Cookie refreshCookie = CookieUtils.createCookie("refreshToken", newTokens.getRefreshToken());
        response.addCookie(refreshCookie);
        return new ApiResponse<TokenResponse>().success(newTokens);
    }
}
