package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
@Api(tags = { "Accounts Controller" }, description = "Provide APIs for account related operation")
public class AccountsController {

    private final AccountsService accountsService;

    @Autowired
    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
        log.info( "Creating account {}", account );
        this.accountsService.createAccount( account );
        return new ResponseEntity<>( HttpStatus.CREATED );
    }

    @GetMapping(path = "/{accountId}")
    public Account getAccount(@PathVariable String accountId) {
        log.info( "Retrieving account for id {}", accountId );
        return this.accountsService.getAccount( accountId );
    }

}
