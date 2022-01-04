package com.db.awmd.challenge.dto;

import com.db.awmd.challenge.domain.Account;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class AccountDto {

    private  String id;
    private  BigDecimal balance;

    private AccountDto(String id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public static AccountDto from(Account account) {
        return new AccountDto(account.getAccountId(), account.getBalance());
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "id='" + id + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}