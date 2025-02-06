package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {
    @NotNull
    @JsonProperty("transaction_id")
    private Long transactionId;
    @NotNull
    @JsonProperty("account_id")
    private Long accountId;
    @NotNull
    @JsonProperty("amount")
    private BigDecimal amount;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("transaction_time")
    private LocalDateTime transactionTime;
    @NotNull
    @JsonProperty("status")
    private TransactionStatus status;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
}
