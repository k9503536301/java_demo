package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {
    @NotNull
    @JsonProperty("account_id")
    private Long accountId;
    @NotNull
    @JsonProperty("client_id")
    private Long clientId;
    @NotNull
    @JsonProperty("account_type")
    private AccountType accountType;
    @NotNull
    @JsonProperty("balance")
    private BigDecimal balance;
    @NotNull
    @JsonProperty("status")
    private AccountStatus status;
    @NotNull
    @JsonProperty("frozen_amount")
    private BigDecimal frozenAmount;
}
