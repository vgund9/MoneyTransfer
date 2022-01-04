package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.dto.AccountDto;
import com.db.awmd.challenge.dto.MoneyTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.IllegalOperationException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransferService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransferServiceTest {

  @Autowired
  private TransferService transferService;
  @Autowired
  private AccountsService accountsService;


  @Before
  public void prepareMockMvc() {
      // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void transferToNotExitSource() throws Exception {
    Account targetAccount = new Account("2");
    targetAccount.setBalance(new BigDecimal(500));
    this.accountsService.createAccount(targetAccount);

    assertThat(this.accountsService.getAccount("1")).isEqualTo(null);
    assertThat(this.accountsService.getAccount("2")).isEqualTo(targetAccount);

    MoneyTransfer moneyTransfer =new MoneyTransfer("1","2", "500");

    try {
      List<AccountDto> accountDtos = this.transferService.transfer( moneyTransfer );
    } catch (IllegalOperationException ex) {
      assertThat(ex.getMessage()).isEqualTo("Source Account(s) doesn't exist.");
    }
    assertThat(this.accountsService.getAccount("1")).isEqualTo(null);
    assertThat(this.accountsService.getAccount("2").getBalance()).isEqualTo(new BigDecimal(500));

  }

  @Test
  public void transferToNotExitTarget() throws Exception {
    Account sourceAccount = new Account("1");
    sourceAccount.setBalance(new BigDecimal(500));
    this.accountsService.createAccount(sourceAccount);

    assertThat(this.accountsService.getAccount("1")).isEqualTo(sourceAccount);
    assertThat(this.accountsService.getAccount("2")).isEqualTo(null);

    MoneyTransfer moneyTransfer =new MoneyTransfer("1","2", "500");

    try {
      List<AccountDto> accountDtos = this.transferService.transfer( moneyTransfer );
    } catch (IllegalOperationException ex) {
      assertThat(ex.getMessage()).isEqualTo("Target Account(s) doesn't exist.");
    }
    assertThat(this.accountsService.getAccount("2")).isEqualTo(null);
    assertThat(this.accountsService.getAccount("1").getBalance()).isEqualTo(new BigDecimal(500));

  }

  @Test
  public void transferToSameAccount() throws Exception {
    Account sourceAccount = new Account("1");
    sourceAccount.setBalance(new BigDecimal(500));
    this.accountsService.createAccount(sourceAccount);

    assertThat(this.accountsService.getAccount("1")).isEqualTo(sourceAccount);

    MoneyTransfer moneyTransfer =new MoneyTransfer("1","1", "500");

    try {
      List<AccountDto> accountDtos = this.transferService.transfer( moneyTransfer );
    } catch (IllegalOperationException ex) {
      assertThat(ex.getMessage()).isEqualTo("Money Transfer can't be performed due same source and destination");
    }
    assertThat(this.accountsService.getAccount("1").getBalance()).isEqualTo(new BigDecimal(500));

  }

  @Test
  public void transfer() throws Exception {
    Account sourceAccount = new Account("1");
    sourceAccount.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(sourceAccount);

    Account targetAccount = new Account("2");
    targetAccount.setBalance(new BigDecimal(0));
    this.accountsService.createAccount(targetAccount);

    assertThat(this.accountsService.getAccount("1")).isEqualTo(sourceAccount);
    assertThat(this.accountsService.getAccount("2")).isEqualTo(targetAccount);

    MoneyTransfer moneyTransfer =new MoneyTransfer("1","2", "500");
    List<AccountDto> accountDtos = this.transferService.transfer( moneyTransfer );
    assertThat( accountDtos.size()).isEqualTo( 2 );
    assertThat(this.accountsService.getAccount("1").getBalance()).isEqualTo(new BigDecimal(500));
    assertThat(this.accountsService.getAccount("2").getBalance()).isEqualTo(new BigDecimal(500));

  }
}
