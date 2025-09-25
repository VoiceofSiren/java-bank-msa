package org.example.bank.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.dto.AccountView;
import org.example.bank.service.AccountReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/read")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Read API", description = "read operation api")
public class ReadController {

    private final AccountReadService accountReadService;

    @Operation(
            summary = "accountNumber GET api",
            description = "accountNumber GET api",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "OK",
                        content = @Content(mediaType = "application/json")
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "BAD REQUEST",
                        content = @Content(mediaType = "application/json")
                )
            }
    )
    @GetMapping("/{accountNumber}")
    public ResponseEntity<org.example.bank.common.ApiResponse<AccountView>> getAccount(
            @Parameter(description = "Account number", required = true)
            @PathVariable(name = "accountNumber") String accountNumber
    ) {
        log.info("read account: " + accountNumber);
        return accountReadService.getAccount(accountNumber);
    }
}
