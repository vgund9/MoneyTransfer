package com.db.awmd.challenge.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
final public class MoneyTransfer {

    @NotNull
    @NotEmpty
    private final String source;

    @NotNull
    @NotEmpty
    private final String target;

    @NotNull
    private final BigDecimal amount;

    public MoneyTransfer(String source, String target, String amount) {
        this.source = source;
        this.target = target;
        this.amount = new BigDecimal(amount);
    }

    public MoneyTransfer() {
        source = "";
        target = "";
        amount = BigDecimal.ZERO;
    }


    @Override
    public String toString() {
        return "MoneyTransfer | amount=" + amount + " " +
                "from sourceAccount='" + source + '\'' +
                "to targetAccount='" + target;
    }
}