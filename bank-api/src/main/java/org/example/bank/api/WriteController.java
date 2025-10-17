package org.example.bank.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.common.ApiResponse;
import org.example.bank.dto.AccountView;
import org.example.bank.request.AccountCreateRequest;
import org.example.bank.request.TransferRequest;
import org.example.bank.service.AccountWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/write")
@Tag(name = "Write API", description = "Write Operation")
public class WriteController {
    private final AccountWriteService accountWriteService;

    @Operation(
            summary = "Create new account",
            description = "Create a new account with specified account holder name and initial balance"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<AccountView>> createAccount(
            @RequestBody AccountCreateRequest accountCreateRequest
            ) {
        log.info("Creates account for user: {}, with initial balance: {}",
                accountCreateRequest.getUserId(),accountCreateRequest.getInitialBalance());
        return accountWriteService.createAccount(accountCreateRequest);
    }

    @Operation(
            summary = "Transfer money from one account to another",
            description = "Transfer the specified amount of money from one account to another",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Transfer successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid amount or insufficient funds"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Account not found"
                    )
            }
    )
    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<String>> transfer(
            @RequestBody TransferRequest transferRequest
    ) {
        log.info("Transfer from {} to {}", transferRequest.getFromAccountNumber(), transferRequest.getToAccountNumber());
        return accountWriteService.transferInternal(transferRequest);
    }
}
