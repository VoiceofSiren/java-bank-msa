package org.example.bank.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.common.ApiResponse;
import org.example.bank.entity.UserReadView;
import org.example.bank.jwt.CookieUtils;
import org.example.bank.jwt.TokenResponse;
import org.example.bank.request.UserLoginRequest;
import org.example.bank.service.UserReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/read")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Read User API", description = "User read operation api")
public class UserReadController {

    private final UserReadService userReadService;

    @PostMapping("/users/log-in")
    public ResponseEntity<ApiResponse<TokenResponse>> logIn(
            @RequestBody UserLoginRequest userLoginRequest,
            HttpServletResponse response
            ) {
        log.info("log in user: {}", userLoginRequest.getEmail());

        ResponseEntity<ApiResponse<TokenResponse>> result = userReadService.getAuth(userLoginRequest);
        if (result.getBody().getSuccess().booleanValue() == true) {
            Cookie refreshCookie = CookieUtils.createCookie(result.getBody().getData().getRefreshToken(), "refreshToken");
            response.addCookie(refreshCookie);
        }
        return result;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserReadView>> showMyInfo(
            @PathVariable(name = "userId") String userId
    ) {
        return userReadService.getUserInfo(userId);
    }
}
