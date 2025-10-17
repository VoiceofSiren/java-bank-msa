package org.example.bank.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.common.ApiResponse;
import org.example.bank.dto.UserView;
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
    public ResponseEntity<ApiResponse<UserView>> logIn(
            @RequestBody UserLoginRequest userLoginRequest
            ) {
        log.info("log in user: {}", userLoginRequest.getEmail());

        return userReadService.getAuth(userLoginRequest);
    }
}
