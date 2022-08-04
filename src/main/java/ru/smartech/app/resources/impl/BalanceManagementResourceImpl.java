package ru.smartech.app.resources.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.smartech.app.dto.BalanceDto;
import ru.smartech.app.dto.TransferDto;
import ru.smartech.app.resources.BalanceManagementResource;
import ru.smartech.app.service.AccountService;
import ru.smartech.app.service.BalanceManager;
import ru.smartech.app.service.SecurityService;

@Slf4j
@RestController
public class BalanceManagementResourceImpl implements BalanceManagementResource {

    private final BalanceManager balanceManager;

    @Autowired
    public BalanceManagementResourceImpl(BalanceManager balanceManager) {
        this.balanceManager = balanceManager;
    }

    @Override
    public ResponseEntity<BalanceDto> transfer(TransferDto transfer) {
        var securityCtx = SecurityService.getAdvancedContext();
        if (securityCtx == null)
            return ResponseEntity.badRequest().build();

        long initiator = securityCtx.getUserId();
        log.debug("Initialize transfer from user #{} to user #{} with amount {}", initiator, transfer.getUserId(), transfer.getAmount());
        var result = balanceManager.transfer(initiator, transfer.getUserId(), transfer.getAmount());
        log.info("Transfer from user #{} to user #{} with amount {} successfully completed", initiator, transfer.getUserId(), transfer.getAmount());
        return ResponseEntity.ok(result);
    }
}
