package com.db.awmd.challenge.service.impl;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.dto.AccountDto;
import com.db.awmd.challenge.dto.MoneyTransfer;
import com.db.awmd.challenge.exception.IllegalOperationException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.challenge.service.TransferService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {
    //lock used to prevent deadlock
    private static final Object lock = new Object();

    @Getter
    AccountsRepository accountsRepository;

    @Getter
    NotificationService notificationService;

    @Autowired
    public TransferServiceImpl(AccountsRepository accountsRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    public List<AccountDto> transfer(final MoneyTransfer transaction) {
        Account source = accountsRepository.getAccount( transaction.getSource() );
        Account target = accountsRepository.getAccount( transaction.getTarget() );
        validateTransaction( source, target, transaction.getAmount() );
        return transferMoney( source, target, transaction.getAmount() );
    }

    private void validateTransaction(Account source, Account target, BigDecimal amount) {
        if (source == null) {
            throw new IllegalOperationException( "Source Account(s) doesn't exist." );
        }
        if (target == null) {
            throw new IllegalOperationException( "Target Account(s) doesn't exist." );
        }
        if (source.equals( target )) {
            throw new IllegalOperationException( "Money Transfer can't be performed due same source and destination" );
        }
    }


    private List<AccountDto> transferMoney(final Account sourceAccount, final Account targetAccount, final BigDecimal amount) {
        class TransferExecutor {
            private List<AccountDto> execute() {
                sourceAccount.debit( amount );
                targetAccount.credit( amount );
                //considering notification services Async
                notificationService.notifyAboutTransfer( sourceAccount, "Dear Customer Amount "+amount+" Debited form your account" );
                notificationService.notifyAboutTransfer( sourceAccount, "Dear Customer Amount "+amount+" Credited in your account" );
                return Collections.unmodifiableList( Arrays.asList( AccountDto.from( sourceAccount ), AccountDto.from( targetAccount ) ) );
            }
        }

        int sourceHash = System.identityHashCode( sourceAccount );
        int targetHash = System.identityHashCode( targetAccount );

        if (sourceHash < targetHash) {
            //synchronized both the account for transaction
            synchronized (sourceAccount) {
                synchronized (targetAccount) {
                    return new TransferExecutor().execute();
                }
            }
        } else if (sourceHash > targetHash) {
            //synchronized both the account for transaction
            synchronized (targetAccount) {
                synchronized (sourceAccount) {
                    return new TransferExecutor().execute();
                }
            }
        } else {
            //lock for same account if involved in multiple transaction
            synchronized (lock) {
                synchronized (sourceAccount) {
                    synchronized (targetAccount) {
                        return new TransferExecutor().execute();
                    }
                }
            }
        }
    }
}
