package ru.smartech.app.service.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.smartech.app.entity.Account;
import ru.smartech.app.exceptions.TransferException;
import ru.smartech.app.schedule.IncrementScheduler;
import ru.smartech.app.service.AccountService;
import ru.smartech.app.service.BalanceManager;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest()
@ActiveProfiles("dev")
@MockBean(IncrementScheduler.class)
@Disabled("Manual test with database only")
class TransactionBalanceManagerTest {

    @Autowired
    @Qualifier("transactionalBalanceManager")
    private BalanceManager transactionalBalanceManager;
    @Autowired
    private AccountService accountService;
    private final ExecutorService executor = Executors.newFixedThreadPool(60);

    @Test
    void transfer() throws InterruptedException {
        final long userOneId = 1L;
        final long userTwoId = 2L;
        final long userThreeId = 3L;
        final Optional<Account> accOne = accountService.findByUser(userOneId);
        final Optional<Account> accTwo = accountService.findByUser(userTwoId);
        final Optional<Account> accThree = accountService.findByUser(userThreeId);
        assertTrue(accOne.isPresent());
        assertTrue(accTwo.isPresent());
        assertTrue(accThree.isPresent());
        System.out.printf("Balance #1: %s\nBalance #2: %s\nBalance #3: %s",
                accOne.get().getBalance(),
                accTwo.get().getBalance(),
                accThree.get().getBalance());

        int transferCount = 40;
        final long transferAmount = 100;
        CountDownLatch cdl = new CountDownLatch(transferCount);
        for (int i = 0; i < transferCount; i++) {
            int k = i % 2;
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
                            transactionalBalanceManager.transfer(userThreeId, userTwoId, new BigDecimal(transferAmount));
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

        long diff = (transferCount / 2 * transferAmount);

        assertEquals(
                accOne.get().getBalance().add(accTwo.get().getBalance()).add(accThree.get().getBalance()),
                resultBalanceOne.add(resultBalanceTwo).add(resultBalanceThree)
        );
        assertEquals(
                accOne.get().getBalance().add(new BigDecimal(-diff)),
                resultBalanceOne
        );
        assertEquals(
                accTwo.get().getBalance().add(new BigDecimal(2 * diff)),
                resultBalanceTwo
        );
        assertEquals(
                accThree.get().getBalance().add(new BigDecimal(-diff)),
                resultBalanceThree
        );
    }

    @Test
    void transfer_insufficientFunds() {
        final long userOneId = 1L;
        final long userTwoId = 2L;
        final Optional<Account> accOne = accountService.findByUser(userOneId);
        final Optional<Account> accTwo = accountService.findByUser(userTwoId);
        assertTrue(accOne.isPresent());
        assertTrue(accTwo.isPresent());

        assertThrows(
                TransferException.class,
                () -> transactionalBalanceManager.transfer(userOneId, userTwoId, accOne.get().getBalance().multiply(BigDecimal.TEN)));
    }
}