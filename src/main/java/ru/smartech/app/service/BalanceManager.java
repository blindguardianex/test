package ru.smartech.app.service;

import ru.smartech.app.dto.BalanceDto;

import java.math.BigDecimal;

public interface BalanceManager {

    BalanceDto transfer(long userIdFrom, long userIdTo, BigDecimal amount);
}
