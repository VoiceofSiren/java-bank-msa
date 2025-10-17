package org.example.bank.jwt;

public class JwtConstants {

    private JwtConstants() {} // 생성자 private 처리로 인스턴스화 방지

    public static final String ACCESS_TOKEN_HEADER_NAME = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    public static final String ACCESS_TOKEN_CATEGORY = "access";
    public static final String REFRESH_TOKEN_CATEGORY = "refresh";

    public static final long ACCESS_TOKEN_EXPIRATION = 1 * 1 * 20 * 1000L;    // 20초
    public static final long REFRESH_TOKEN_EXPIRATION = 1 * 1 * 2 * 60 * 1000L; // 2분
}
