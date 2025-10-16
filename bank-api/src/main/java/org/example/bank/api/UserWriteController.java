package org.example.bank.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.common.ApiResponse;
import org.example.bank.dto.UserView;
import org.example.bank.request.UserCreateRequest;
import org.example.bank.service.UserWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/write")
@Tag(name = "User Write API", description = "User write operation api")
public class UserWriteController {

    private final UserWriteService userWriteService;

    @Operation(
            summary = "Create new user",
            description = "Create a new user with specified username"
    )
    @PostMapping("/users/sign-up")
    public ResponseEntity<ApiResponse<UserView>> signUp(
            @RequestBody UserCreateRequest userCreateRequest
            ) {
        log.info("Creates user for: {}",
                userCreateRequest.getUsername());
        return userWriteService.createUser(userCreateRequest);
    }


}
