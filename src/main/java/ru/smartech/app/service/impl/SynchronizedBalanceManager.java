package ru.smartech.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smartech.app.dto.BalanceDto;
import ru.smartech.app.entity.Account;
import ru.smartech.app.exceptions.NonExistEntity;
import ru.smartech.app.exceptions.TransferException;
import ru.smartech.app.service.AccountService;
import ru.smartech.app.service.BalanceManager;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service("synchronizedBalanceManager")
@Primary
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
        BalanceDto transferResult;
        lock.lock();
        try {
            Account from = getAccount(userIdFrom);
            if (from.getBalance().compareTo(amount) < 0) {
                log.info("IN transfer -> insufficient funds to transfer: on account {}, required {}", from.getBalance(), amount);
                throw new IllegalArgumentException("Insufficient funds to transfer: on account " + from.getBalance() + ", required " + amount);
            }
            Account to = getAccount(userIdTo);
            transferResult = transfer(from, to, amount);
        } catch (Exception e) {
            throw new TransferException(e.getMessage());
        } finally {
            lock.unlock();
        }
        log.info("IN transfer -> successfully transfer {} from user #{} to user #{}", amount, userIdFrom, userIdTo);
        return transferResult;
    }

    private Account getAccount(long userId) {
        return accountService.findByUser(userId)
                .orElseThrow(() -> {
                    log.error("IN transfer ->  account with user ID {} not exist", userId);
                    throw new NonExistEntity("Account with user ID " + userId + "not exist");
                });
    }

    private BalanceDto transfer(Account from, Account to, BigDecimal amount) {
        log.info("Start balance\n\t\tFrom balance #{}: {}\n\t\tTo balance #{}: {}", from.getId(), from.getBalance(), to.getId(), to.getBalance());
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountService.update(from);
        try {
            accountService.update(to);
        } catch (Exception e) {
            log.info("IN transfer -> was happen exception when update account-recipient. Cancelled transaction...");
            from.setBalance(from.getBalance().add(amount));
            accountService.update(from);
            log.error("IN transfer -> transfer {} from acc #{} to acc #{} successfully canceled", amount, from.getId(), to.getId());
            throw new RuntimeException(e.getMessage());
        }
        log.info("End balance\n\t\tFrom balance #{}: {}\n\t\tTo balance #{}: {}", from.getId(), from.getBalance(), to.getId(), to.getBalance());
        return BalanceDto.builder().userId(from.getId()).currentAmount(from.getBalance()).build();
    }
}
