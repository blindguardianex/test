package ru.smartech.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.smartech.app.entity.Account;
import ru.smartech.app.repository.AccountRepository;
import ru.smartech.app.service.AccountService;

import java.util.Optional;

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
}
