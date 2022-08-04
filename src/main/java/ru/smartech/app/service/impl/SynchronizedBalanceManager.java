package ru.smartech.app.service.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.smartech.app.dto.BalanceDto;
import ru.smartech.app.entity.Account;
import ru.smartech.app.exceptions.NonExistEntity;
import ru.smartech.app.service.AccountService;
import ru.smartech.app.service.BalanceManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SynchronizedBalanceManager implements BalanceManager {

    private final AccountService accountService;
    private final Lock lock;

    @Autowired
    public SynchronizedBalanceManager(AccountService accountService) {
        this.accountService = accountService;
        this.lock = new ReentrantLock();
    }

    @Override
    public BalanceDto transfer(long userIdFrom, long userIdTo, BigDecimal amount) {
        lock.lock();
        BalanceDto transferResult;
        try {
            Account from = getAccount(userIdFrom);
            if (from.getBalance().compareTo(amount) < 0){
                log.info("Insufficient funds to transfer: on account {}, required {}", from.getBalance(), amount);
                return BalanceDto.builder().userId(from.getId()).currentAmount(from.getBalance()).build();
            }
            Account to = getAccount(userIdTo);
            transferResult = transfer(from, to, amount);
        } finally {
            lock.unlock();
        }
        return transferResult;
    }

    private Account getAccount(long userId) {
        return accountService.findByUser(userId)
                .orElseThrow(() -> {
                    log.error("IN transfer ->  account with user ID {} not exist", userId);
                    throw new NonExistEntity("Account with user ID "+ userId +"not exist");
                });
    }

    private BalanceDto transfer(Account from, Account to, BigDecimal amount) {
        log.info("Start balance\n\t\tFrom balance #{}: {}\n\t\tTo balance #{}: {}", from.getId(), from.getBalance(), to.getId(), to.getBalance());
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountService.update(from);
        accountService.update(to);
        log.info("End balance\n\t\tFrom balance #{}: {}\n\t\tTo balance #{}: {}", from.getId(), from.getBalance(), to.getId(), to.getBalance());
        return BalanceDto.builder().userId(from.getId()).currentAmount(from.getBalance()).build();
    }
}
