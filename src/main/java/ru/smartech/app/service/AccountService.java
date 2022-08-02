package ru.smartech.app.service;

import ru.smartech.app.entity.Account;

import java.util.Optional;

public interface AccountService {

    Optional<Account> findByUser(long userId);
}
