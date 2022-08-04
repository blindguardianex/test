package ru.smartech.app.service;

import ru.smartech.app.dto.BalanceDto;
import ru.smartech.app.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AccountService {

    Optional<Account> findByUser(long userId);

    Account update(Account account);

    List<Account> getAll();
}
