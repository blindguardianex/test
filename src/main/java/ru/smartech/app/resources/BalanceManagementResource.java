package ru.smartech.app.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.smartech.app.dto.BalanceDto;
import ru.smartech.app.dto.EmailDto;
import ru.smartech.app.dto.TransferDto;

import javax.validation.Valid;

@Tag(
        name = "balance-management",
        description = "Управление балансом")
@RequestMapping("/api/v1/balance-management")
public interface BalanceManagementResource {


    @Operation(summary = "Присоединить почту к пользователю")
    @PostMapping("/transfer")
    ResponseEntity<BalanceDto> transfer(
            @Parameter(description = "ДТО трансфера") @Valid @RequestBody TransferDto transfer);
}
