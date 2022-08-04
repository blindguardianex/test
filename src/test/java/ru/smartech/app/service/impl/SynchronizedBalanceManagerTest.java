package ru.smartech.app.service.impl;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.smartech.app.service.BalanceManager;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class SynchronizedBalanceManagerTest {

    @Autowired
    private BalanceManager balanceManager;
    private final ExecutorService executor = Executors.newFixedThreadPool(40);

    @Test
    void transfer_1() throws InterruptedException {
        int count = 20;
        CountDownLatch cdl = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                balanceManager.transfer(1, 2, new BigDecimal(10));
                cdl.countDown();
            });
        }
        cdl.await();
    }

    @Test
    void transfer_2() throws InterruptedException {
        int count = 40;
        CountDownLatch cdl = new CountDownLatch(count);
        for (int i = 0; i < count/2; i++) {
            executor.submit(() -> {
                balanceManager.transfer(1, 2, new BigDecimal(10));
                cdl.countDown();
            });
        }
        for (int i = 0; i < count/2; i++) {
            executor.submit(() -> {
                balanceManager.transfer(2, 1, new BigDecimal(10));
                cdl.countDown();
            });
        }
        cdl.await();
    }
}