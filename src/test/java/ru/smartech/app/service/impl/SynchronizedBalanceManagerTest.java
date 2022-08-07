package ru.smartech.app.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.smartech.app.entity.Account;
import ru.smartech.app.entity.User;
import ru.smartech.app.exceptions.TransferException;
import ru.smartech.app.service.AccountService;
import ru.smartech.app.service.BalanceManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(
        classes = {
                SynchronizedBalanceManager.class
        }
)
class SynchronizedBalanceManagerTest {

    @Autowired
    @Qualifier("synchronizedBalanceManager")
    private BalanceManager synchronizedBalanceManager;
    @MockBean
    private AccountService accountServiceMock;
    private final ExecutorService executor = Executors.newFixedThreadPool(60);

    @Test
    void transfer_1() throws InterruptedException {
        final long userFromId = 1L;
        final long userToId = 2L;
        final long startBalanceFrom = 1000;
        final long startBalanceTo = 1500;
        Map<Long, Account> mockAccounts = createMockAccounts(2, startBalanceFrom, startBalanceTo);

        Mockito.when(accountServiceMock.findByUser(anyLong()))
                .thenAnswer(invoc -> {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(300));
                    long userId = invoc.getArgument(0, Long.class);
                    return Optional.of(copy(mockAccounts.get(userId)));
                });
        Mockito.when(accountServiceMock.update(any()))
                .thenAnswer(invoc -> {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(300));
                    Account acc = invoc.getArgument(0, Account.class);
                    mockAccounts.put(acc.getUser().getId(), acc);
                    return copy(acc);
                });

        int transferCount = 20;
        final long transferAmount = 10;
        CountDownLatch cdl = new CountDownLatch(transferCount);
        for (int i = 0; i < transferCount; i++) {
            executor.submit(() -> {
                try {
                    synchronizedBalanceManager.transfer(userFromId, userToId, new BigDecimal(transferAmount));
                } finally {
                    cdl.countDown();
                }
            });
        }
        cdl.await();

        assertEquals(
                mockAccounts.get(userFromId).getBalance(),
                new BigDecimal(startBalanceFrom - (transferCount * transferAmount))
        );
        assertEquals(
                mockAccounts.get(userToId).getBalance(),
                new BigDecimal(startBalanceTo + (transferCount * transferAmount))
        );
    }

    @Test
    void transfer_2() throws InterruptedException {
        final long userOneId = 1L;
        final long userTwoId = 2L;
        final long userThreeId = 3L;
        final long startBalanceOne = 1000;
        final long startBalanceTwo = 1500;
        final long startBalanceThree = 1800;
        Map<Long, Account> mockAccounts = createMockAccounts(3, startBalanceOne, startBalanceTwo, startBalanceThree);

        Mockito.when(accountServiceMock.findByUser(anyLong()))
                .thenAnswer(invoc -> {
                    long userId = invoc.getArgument(0, Long.class);
                    return Optional.of(copy(mockAccounts.get(userId)));
                });
        Mockito.when(accountServiceMock.update(any()))
                .thenAnswer(invoc -> {
                    Account acc = invoc.getArgument(0, Account.class);
                    mockAccounts.put(acc.getUser().getId(), acc);
                    return copy(acc);
                });


        int transferCount = 80;
        final long transferAmount = 10;
        CountDownLatch cdl = new CountDownLatch(transferCount);
        for (int i = 0; i < transferCount; i++) {
            int k = i % 4;
            switch (k) {
                case 0: {
                    executor.submit(() -> {
                        try {
                            synchronizedBalanceManager.transfer(userOneId, userTwoId, new BigDecimal(transferAmount));
                        } finally {
                            cdl.countDown();
                        }
                    });
                    break;
                }
                case 1: {
                    executor.submit(() -> {
                        try {
                            synchronizedBalanceManager.transfer(userTwoId, userThreeId, new BigDecimal(transferAmount));
                        } finally {
                            cdl.countDown();
                        }
                    });
                    break;
                }
                case 2: {
                    executor.submit(() -> {
                        try {
                            synchronizedBalanceManager.transfer(userTwoId, userOneId, new BigDecimal(transferAmount));
                        } finally {
                            cdl.countDown();
                        }
                    });
                    break;
                }
                case 3: {
                    executor.submit(() -> {
                        try {
                            synchronizedBalanceManager.transfer(userThreeId, userOneId, new BigDecimal(transferAmount));
                        } finally {
                            cdl.countDown();
                        }
                    });
                    break;
                }
            }
        }
        cdl.await();

        System.out.printf("Balance #1: %s\nBalance #2: %s\nBalance #3: %s",
                mockAccounts.get(userOneId).getBalance(),
                mockAccounts.get(userTwoId).getBalance(),
                mockAccounts.get(userThreeId).getBalance());

        long diff = (transferCount / 4 * transferAmount);

        assertEquals(
                new BigDecimal(startBalanceOne + startBalanceTwo + startBalanceThree),
                mockAccounts.get(userOneId).getBalance().add(mockAccounts.get(userTwoId).getBalance()).add(mockAccounts.get(userThreeId).getBalance())
        );
        assertEquals(
                new BigDecimal(startBalanceOne - diff + diff + diff),
                mockAccounts.get(userOneId).getBalance()
        );
        assertEquals(
                new BigDecimal(startBalanceTwo + diff - diff - diff),
                mockAccounts.get(userTwoId).getBalance()
        );
        assertEquals(
                new BigDecimal(startBalanceThree + diff - diff),
                mockAccounts.get(userThreeId).getBalance()
        );
    }

    @Test
    void transfer_insufficientFunds() {
        final long userFromId = 1L;
        final long userToId = 2L;
        final long startBalanceFrom = 1000;
        Map<Long, Account> mockAccounts = createMockAccounts(1, startBalanceFrom);

        Mockito.when(accountServiceMock.findByUser(anyLong()))
                .thenAnswer(invoc -> {
                    long userId = invoc.getArgument(0, Long.class);
                    return Optional.of(mockAccounts.get(userId));
                });
        assertThrows(
                TransferException.class,
                () -> synchronizedBalanceManager.transfer(userFromId, userToId, new BigDecimal(startBalanceFrom * 2)));
    }

    @Test
    void transfer_transactionalError(){
        final long userFromId = 1L;
        final long userToId = 2L;
        final long startBalanceFrom = 1000;
        final long startBalanceTo = 1500;
        Map<Long, Account> mockAccounts = createMockAccounts(2, startBalanceFrom, startBalanceTo);

        Mockito.when(accountServiceMock.findByUser(anyLong()))
                .thenAnswer(invoc -> {
                    long userId = invoc.getArgument(0, Long.class);
                    return Optional.of(copy(mockAccounts.get(userId)));
                });
        Mockito.when(accountServiceMock.update(any()))
                .thenAnswer(invoc -> {
                    Account acc = invoc.getArgument(0, Account.class);
                    if (acc.getId()==userToId){
                        throw new RuntimeException("Any transactional error");
                    }
                    mockAccounts.put(acc.getUser().getId(), acc);
                    return copy(acc);
                });
        assertThrows(
                TransferException.class,
                () -> synchronizedBalanceManager.transfer(userFromId, userToId, new BigDecimal(startBalanceFrom/2)));
        assertEquals(new BigDecimal(startBalanceFrom), mockAccounts.get(userFromId).getBalance());
        assertEquals(new BigDecimal(startBalanceTo), mockAccounts.get(userToId).getBalance());
    }

    private Map<Long, Account> createMockAccounts(int count, long... balances) {
        Map<Long, Account> accounts = new HashMap<>();
        for (long i = 1; i <= count; i++) {
            accounts.put(
                    i,
                    new Account()
                            .setId(i)
                            .setBalance(BigDecimal.valueOf(balances[(int) i - 1]))
                            .setUser(new User().setId(i)));
        }

        return accounts;
    }

    private Account copy(Account acc){
        return new Account()
                .setId(acc.getId())
                .setBalance(acc.getBalance())
                .setUser(acc.getUser());
    }
}