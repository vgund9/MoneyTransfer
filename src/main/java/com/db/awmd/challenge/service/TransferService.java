package com.db.awmd.challenge.service;

import com.db.awmd.challenge.dto.AccountDto;
import com.db.awmd.challenge.dto.MoneyTransfer;

import java.util.List;

public interface TransferService {
    List<AccountDto> transfer(final MoneyTransfer transaction);
}
