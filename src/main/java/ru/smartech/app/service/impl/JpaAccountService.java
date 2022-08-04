package ru.smartech.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.smartech.app.dto.BalanceDto;
import ru.smartech.app.entity.Account;
import ru.smartech.app.exceptions.NonExistEntity;
import ru.smartech.app.repository.AccountRepository;
import ru.smartech.app.service.AccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class JpaAccountService implements AccountService {

    private final AccountRepository repository;

    @Autowired
    public JpaAccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Account> findByUser(long userId) {
        log.debug("IN findByUser -> find account by user ID {}", userId);
        var account = repository.findByUserId(userId);
        if (account.isPresent()){
            log.info("IN findByUser -> by user ID {} was found account with ID {}", userId, account.get().getId());
        } else {
            log.info("IN findByUser -> by user ID {} was NOT found account", userId);
        }
        return account;
    }

    @Override
    public Account update(Account account) {
        return repository.save(account);
    }

    @Override
    public List<Account> getAll() {
        return repository.findAll();
    }
}
