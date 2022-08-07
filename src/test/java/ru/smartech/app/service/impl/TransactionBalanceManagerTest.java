package ru.smartech.app.service.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.smartech.app.entity.Account;
import ru.smartech.app.repository.AccountRepository;
import ru.smartech.app.schedule.IncrementScheduler;
import ru.smartech.app.service.AccountService;
import ru.smartech.app.service.BalanceManager;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest()
@ActiveProfiles("dev")
@MockBean(IncrementScheduler.class)
@Disabled("Manual test with database only")
class TransactionBalanceManagerTest {

    @Autowired
    private BalanceManager transactionalBalanceManager;
    @Autowired
    private AccountService accountService;
    private final ExecutorService executor = Executors.newFixedThreadPool(60);

    @Test
    void transfer_1() throws InterruptedException {
        final long userFromId = 1L;
        final long userToId = 2L;

        final Optional<Account> accFrom = accountService.findByUser(userFromId);
        final Optional<Account> accTo = accountService.findByUser(userToId);
        assertTrue(accFrom.isPresent());
        assertTrue(accTo.isPresent());

        int transferCount = 20;
        final long transferAmount = 10;
        CountDownLatch cdl = new CountDownLatch(transferCount);
        for (int i = 0; i < transferCount; i++) {
            executor.submit(() -> {
                try {
                    transactionalBalanceManager.transfer(userFromId, userToId, new BigDecimal(transferAmount));
                } finally {
                    cdl.countDown();
                }
            });
        }
        cdl.await();

        assertEquals(
                accFrom.get().getBalance().subtract(new BigDecimal(transferCount * transferAmount)),
                accountService.findByUser(userFromId).get().getBalance()
        );
        assertEquals(
                accFrom.get().getBalance().add(new BigDecimal(transferCount * transferAmount)),
                accountService.findByUser(userToId).get().getBalance()
        );
    }

    @Test
    void transfer_2() throws InterruptedException {
        final long userOneId = 1L;
        final long userTwoId = 2L;
        final long userThreeId = 3L;
        final Optional<Account> accOne = accountService.findByUser(userOneId);
        final Optional<Account> accTwo = accountService.findByUser(userTwoId);
        final Optional<Account> accThree = accountService.findByUser(userThreeId);

        int transferCount = 80;
        final long transferAmount = 10;
        CountDownLatch cdl = new CountDownLatch(transferCount);
        for (int i = 0; i < transferCount; i++) {
            int k = i % 4;
            switch (k) {
                case 0: {
                    executor.submit(() -> {
                        try {
                            transactionalBalanceManager.transfer(userOneId, userTwoId, new BigDecimal(transferAmount));
                        } finally {
                            cdl.countDown();
                        }
                    });
                    break;
                }
                case 1: {
                    executor.submit(() -> {
                        try {
                            transactionalBalanceManager.transfer(userTwoId, userThreeId, new BigDecimal(transferAmount));
                        } finally {
                            cdl.countDown();
                        }
                    });
                    break;
                }
                case 2: {
                    executor.submit(() -> {
                        try {
                            transactionalBalanceManager.transfer(userTwoId, userOneId, new BigDecimal(transferAmount));
                        } finally {
                            cdl.countDown();
                        }
                    });
                    break;
                }
                case 3: {
                    executor.submit(() -> {
                        try {
                            transactionalBalanceManager.transfer(userThreeId, userOneId, new BigDecimal(transferAmount));
                        } finally {
                            cdl.countDown();
                        }
                    });
                    break;
                }
            }
        }
        cdl.await();

        var resultBalanceOne = accountService.findByUser(userOneId).get().getBalance();
        var resultBalanceTwo = accountService.findByUser(userTwoId).get().getBalance();
        var resultBalanceThree = accountService.findByUser(userThreeId).get().getBalance();


        System.out.printf("Balance #1: %s\nBalance #2: %s\nBalance #3: %s",
                resultBalanceOne,
                resultBalanceTwo,
                resultBalanceThree);

        long diff = (transferCount / 4 * transferAmount);

        assertEquals(
                accOne.get().getBalance().add(accTwo.get().getBalance()).add(accThree.get().getBalance()),
                resultBalanceOne.add(resultBalanceTwo).add(resultBalanceThree)
        );
        assertEquals(
                accOne.get().getBalance().add(new BigDecimal(-diff + diff + diff)),
                resultBalanceOne
        );
        assertEquals(
                accTwo.get().getBalance().add(new BigDecimal(diff - diff - diff)),
                resultBalanceTwo
        );
        assertEquals(
                accThree.get().getBalance().add(new BigDecimal(diff - diff)),
                resultBalanceThree
        );
    }
}