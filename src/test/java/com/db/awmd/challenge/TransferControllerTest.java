package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransferService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private TransferService transferService;
  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void createAccountTransferMoney() throws Exception {
    Account source = new Account( "1", BigDecimal.valueOf( 1000) );
    Account target = new Account( "2");
    accountsService.createAccount(  source);

    accountsService.createAccount(  target);

    Account sourceAccount = accountsService.getAccount("1");
    assertThat(sourceAccount.getAccountId()).isEqualTo("1");
    assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1000");

    Account targetAccount = accountsService.getAccount("2");
    assertThat(targetAccount.getAccountId()).isEqualTo("2");
    assertThat(targetAccount.getBalance()).isEqualByComparingTo("0");

    this.mockMvc.perform(post("/v1/transactions/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"source\":\"1\",\"target\":\"2\",\"amount\":500}")).andExpect(status().isOk());

    sourceAccount = accountsService.getAccount("1");
    assertThat(sourceAccount.getAccountId()).isEqualTo("1");
    assertThat(sourceAccount.getBalance()).isEqualByComparingTo("500");

    targetAccount = accountsService.getAccount("2");
    assertThat(targetAccount.getAccountId()).isEqualTo("2");
    assertThat(targetAccount.getBalance()).isEqualByComparingTo("500");

  }

  @Test
  public void TransferMoneyToSameAccount() throws Exception {
    Account source = new Account( "1", BigDecimal.valueOf( 1000) );
    accountsService.createAccount(  source);

    Account sourceAccount = accountsService.getAccount("1");
    assertThat(sourceAccount.getAccountId()).isEqualTo("1");
    assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1000");

    this.mockMvc.perform(post("/v1/transactions/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"source\":\"1\",\"target\":\"1\",\"amount\":500}")).andExpect(status().isBadRequest());

    sourceAccount = accountsService.getAccount("1");
    assertThat(sourceAccount.getAccountId()).isEqualTo("1");
    assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1000");

  }

  @Test
  public void transferMoneyMoreThenAvailableBalance() throws Exception {
    Account source = new Account( "1", BigDecimal.valueOf( 1000) );
    Account target = new Account( "2");
    accountsService.createAccount(  source);

    accountsService.createAccount(  target);

    Account sourceAccount = accountsService.getAccount("1");
    assertThat(sourceAccount.getAccountId()).isEqualTo("1");
    assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1000");

    Account targetAccount = accountsService.getAccount("2");
    assertThat(targetAccount.getAccountId()).isEqualTo("2");
    assertThat(targetAccount.getBalance()).isEqualByComparingTo("0");

    this.mockMvc.perform(post("/v1/transactions/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"source\":\"1\",\"target\":\"2\",\"amount\":5000}")).andExpect(status().isBadRequest());

    sourceAccount = accountsService.getAccount("1");
    assertThat(sourceAccount.getAccountId()).isEqualTo("1");
    assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1000");

    targetAccount = accountsService.getAccount("2");
    assertThat(targetAccount.getAccountId()).isEqualTo("2");
    assertThat(targetAccount.getBalance()).isEqualByComparingTo("0");

  }

  @Test
  public void transferNegativeAmount() throws Exception {
    Account source = new Account( "1", BigDecimal.valueOf( 1000) );
    Account target = new Account( "2");
    accountsService.createAccount(  source);

    accountsService.createAccount(  target);

    Account sourceAccount = accountsService.getAccount("1");
    assertThat(sourceAccount.getAccountId()).isEqualTo("1");
    assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1000");

    Account targetAccount = accountsService.getAccount("2");
    assertThat(targetAccount.getAccountId()).isEqualTo("2");
    assertThat(targetAccount.getBalance()).isEqualByComparingTo("0");

    this.mockMvc.perform(post("/v1/transactions/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"source\":\"1\",\"target\":\"2\",\"amount\":-5000}")).andExpect(status().isBadRequest());

    sourceAccount = accountsService.getAccount("1");
    assertThat(sourceAccount.getAccountId()).isEqualTo("1");
    assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1000");

    targetAccount = accountsService.getAccount("2");
    assertThat(targetAccount.getAccountId()).isEqualTo("2");
    assertThat(targetAccount.getBalance()).isEqualByComparingTo("0");

  }

  @Test
  public void sourceAccountDosentExists() throws Exception {
    Account target = new Account( "2");
        accountsService.createAccount(  target);

    Account targetAccount = accountsService.getAccount("2");
    assertThat(targetAccount.getAccountId()).isEqualTo("2");
    assertThat(targetAccount.getBalance()).isEqualByComparingTo("0");

    this.mockMvc.perform(post("/v1/transactions/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"source\":\"1\",\"target\":\"2\",\"amount\":10}")).andExpect(status().isBadRequest());

    Account sourceAccount = accountsService.getAccount("1");
    assertThat(sourceAccount==null);

    targetAccount = accountsService.getAccount("2");
    assertThat(targetAccount.getAccountId()).isEqualTo("2");
    assertThat(targetAccount.getBalance()).isEqualByComparingTo("0");

  }

}
