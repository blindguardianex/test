package ru.smartech.app.schedule;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.smartech.app.entity.Account;
import ru.smartech.app.service.AccountService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class IncrementScheduler {

    private final AccountService accountService;
    private final Map<Long, IncrementInfo> incrementInfos = new ConcurrentHashMap<>();

    @Autowired
    public IncrementScheduler(AccountService accountService) {
        this.accountService = accountService;
    }

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    public void increment() {
        log.debug("Start increment");
        var accounts = accountService.getAll();
        accounts.stream()
                .filter(acc -> !incrementInfos.containsKey(acc.getId()))
                .forEach(acc -> incrementInfos.put(
                        acc.getId(),
                        new IncrementInfo(acc.getId(),
                                acc.getBalance(),
                                acc.getBalance(),
                                acc.getBalance().multiply(BigDecimal.valueOf(2.07)),
                                false
                        )
                ));
        incrementInfos.values()
                .forEach(ii -> {
                    if (ii.isCompleted())
                        return;

                    var account = accounts.stream().filter(acc -> acc.getId() == ii.getAccId())
                            .findFirst()
                            .get();
                    BigDecimal amount = account.getBalance().multiply(BigDecimal.valueOf(1.1));
                    ii.setCurrentAmount(amount);
                    if (amount.compareTo(ii.maxAmount) > 0) {
                        ii.setCompleted(true);
                        incrementInfos.put(ii.getAccId(), ii);
                        log.debug("Incrementing balance for acc #{} is completed. \nStart amount: {}\nCurrent amount: {}", ii.getAccId(), ii.getStartAmount(), ii.getCurrentAmount());
                    } else {
                        ii.setCurrentAmount(amount);
                        incrementInfos.put(ii.getAccId(), ii);
                        account.setBalance(amount);
                        accountService.update(account);
                        log.debug("Incrementing balance for acc #{}. \nStart amount: {}\nCurrent amount: {}", ii.getAccId(), ii.getStartAmount(), ii.getCurrentAmount());
                    }
                });
    }

    @Builder
    @Getter
    private static class IncrementInfo {

        private final long accId;
        private final BigDecimal startAmount;
        @Setter
        private BigDecimal currentAmount;
        private final BigDecimal maxAmount;
        @Setter
        private boolean completed;
    }
}
