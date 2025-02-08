package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountConsumer {
    private final AccountService accountService;

    @KafkaListener(
            groupId = "${t1.kafka.consumer.group-id}",
            topics = "t1_demo_accounts",
            containerFactory = "accountKafkaListenerContainerFactory"
    )
    public void consumeAccountMessage(AccountDto accountDto){
        log.debug("Account consumer: start handler working");
        try {
            Account account = accountService.toEntity(accountDto);
            Account createdAccount = accountService.createAccount(account);

            log.info("Created Account: {}", createdAccount);
        } catch (Exception e) {
            log.error("Failed to save account: {}", e.getMessage());
        }

        log.debug("Account consumer: end handler working");
    }
}
