package com.db.awmd.challenge.web;

import com.db.awmd.challenge.dto.AccountDto;
import com.db.awmd.challenge.dto.MoneyTransfer;
import com.db.awmd.challenge.service.TransferService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
@Slf4j
public class TransactionController {
    @Getter
    private TransferService transferService;

    @Autowired
    public TransactionController(TransferService transferService) {
        this.transferService = transferService;
    }


    @PostMapping(value = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountDto>> transfer(@RequestBody @Valid MoneyTransfer moneyTransfer) {
        log.debug( "Retrieving account for id {}", moneyTransfer );
        List<AccountDto> result = transferService.transfer( moneyTransfer );
        return new ResponseEntity<>( result, HttpStatus.OK );
    }

}
